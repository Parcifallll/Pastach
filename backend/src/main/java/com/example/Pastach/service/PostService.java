package com.example.Pastach.service;

import com.example.Pastach.dto.mapper.PostMapper;
import com.example.Pastach.dto.post.PostCreateDTO;
import com.example.Pastach.dto.post.PostResponseDTO;
import com.example.Pastach.dto.post.PostUpdateDTO;
import com.example.Pastach.exception.PostNotFoundException;
import com.example.Pastach.exception.UserNotFoundException;
import com.example.Pastach.kafka.KafkaProducerService;
import com.example.Pastach.model.Post;
import com.example.Pastach.model.RoleEnum;
import com.example.Pastach.model.User;
import com.example.Pastach.repository.PostRepository;
import com.example.Pastach.repository.UserRepository;
import com.example.Pastach.security.RequireNonLockedUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostMapper postMapper;
    private final KafkaProducerService kafkaProducer;
    private final RecommendationGrpcClient recommendationGrpcClient;


    @PreAuthorize("isAuthenticated()")
    @RequireNonLockedUser
    @Transactional
    public PostResponseDTO create(PostCreateDTO dto, Long authorId) {
        if (!dto.hasContent()) throw new IllegalArgumentException("Post can't be empty");

        // Save post to database
        Post post = postMapper.toEntity(dto);
        post.setAuthorId(authorId);
        post = postRepository.save(post);

        kafkaProducer.sendPostCreated(post);

        // Return response
        return postMapper.toResponseDto(post);
    }

    @Transactional(readOnly = true)
    public PostResponseDTO getById(Long postId) {
        return postRepository.findById(postId)
                .map(postMapper::toResponseDto)
                .orElseThrow(() -> new PostNotFoundException(postId));
    }

    @Transactional(readOnly = true)
    public Page<PostResponseDTO> getAll(Pageable pageable) {
        Page<Post> page = postRepository.findAll(pageable);
        return page.map(postMapper::toResponseDto);
    }

    @Transactional(readOnly = true)
    public Page<PostResponseDTO> getByAuthorId(Long authorId, Pageable pageable) {
        if (!userRepository.existsById(authorId)) {
            throw new UserNotFoundException(authorId);
        }

        Page<Post> page = postRepository.findByAuthorId(authorId, pageable);
        return page.map(postMapper::toResponseDto);
    }


    @PreAuthorize("isAuthenticated()")
    @RequireNonLockedUser
    @Transactional
    public PostResponseDTO updateById(Long postId, PostUpdateDTO dto, @AuthenticationPrincipal User curUser) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(postId));

        if (!dto.hasContent()) throw new IllegalArgumentException("Post can't be empty");
        if (!post.getAuthorId().equals(curUser.getId())) {
            throw new AccessDeniedException("You are not authorized to edit this post.");
        }

        // Check if text changed (for embedding recalculation)
        String oldText = post.getText();

        postMapper.updateFromDto(dto, post);
        post = postRepository.save(post);

        // Send event to Kafka if text changed
        String newText = post.getText();
        if ((oldText == null && newText != null) ||
                (oldText != null && !oldText.equals(newText))) {
            kafkaProducer.sendPostUpdated(post);
        }

        return postMapper.toResponseDto(post);
    }

    @PreAuthorize("isAuthenticated()")
    @RequireNonLockedUser
    @Transactional
    public void deleteById(Long postId, @AuthenticationPrincipal User currentUser) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(postId));

        boolean isAuthor = post.getAuthorId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getRoles().stream().anyMatch(r -> r.getName() == RoleEnum.ADMIN);
        if (!isAuthor && !isAdmin) {
            throw new AccessDeniedException("You are not authorized to delete this post.");
        }

        // Delete from database
        postRepository.delete(post);

        // Send event to Kafka
        kafkaProducer.sendPostDeleted(postId);
    }

    @PreAuthorize("isAuthenticated()")
    @Async("taskExecutor")  // tasks pool from config
    @Transactional(readOnly = true)
    public CompletableFuture<List<PostResponseDTO>> getRecommendedPosts(Long userId, int limit) {
        log.info("Request for recommendations for userId={}, limit={}", userId, limit);

        CompletableFuture<List<Long>> idsFuture = recommendationGrpcClient.getRecommendedPostIdsAsync(userId, limit, true);

        return idsFuture.thenCompose(recommendedIds -> {
                    if (recommendedIds.isEmpty()) {
                        log.warn("Recommendations were not received, fallback to recent posts");
                        return CompletableFuture.supplyAsync(() -> {
                            Pageable pageable = PageRequest.of(0, limit);
                            List<Post> recentPosts = postRepository.findTopNByOrderByCreatedAtDesc(pageable).getContent();
                            return recentPosts.stream().map(postMapper::toResponseDto).collect(Collectors.toList());
                        });
                    } else {
                        Long[] idsArray = recommendedIds.toArray(new Long[0]);
                        return CompletableFuture.supplyAsync(() -> {
                            List<Post> posts = postRepository.findAllByIdInOrderByField(idsArray);
                            return posts.stream().map(postMapper::toResponseDto).collect(Collectors.toList());
                        });
                    }
                })
                .orTimeout(30, TimeUnit.SECONDS) // if CompletableFuture<> did not receive from DB posts
                .exceptionally(e -> {
                    log.error("Error in getRecommendedPosts: {}", e.getMessage(), e);
                    return Collections.emptyList();  // fallback to error
                });
    }

}