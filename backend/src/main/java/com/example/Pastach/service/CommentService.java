package com.example.Pastach.service;

import com.example.Pastach.dto.comment.CommentCreateDTO;
import com.example.Pastach.dto.comment.CommentResponseDTO;
import com.example.Pastach.dto.comment.CommentUpdateDTO;
import com.example.Pastach.dto.mapper.CommentMapper;
import com.example.Pastach.exception.CommentNotFoundException;
import com.example.Pastach.exception.PostNotFoundException;
import com.example.Pastach.kafka.KafkaProducerService;
import com.example.Pastach.model.Comment;
import com.example.Pastach.model.Post;
import com.example.Pastach.model.ReactionTargetType;
import com.example.Pastach.model.RoleEnum;
import com.example.Pastach.model.User;
import com.example.Pastach.repository.CommentRepository;
import com.example.Pastach.repository.PostRepository;
import com.example.Pastach.repository.ReactionRepository;
import com.example.Pastach.security.RequireNonLockedUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final CommentMapper commentMapper;
    private final KafkaProducerService kafkaProducerService;


    @PreAuthorize("isAuthenticated()")
    @RequireNonLockedUser
    @Transactional
    public CommentResponseDTO create(Long postId, CommentCreateDTO dto, @AuthenticationPrincipal User user) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException(postId));

        Comment comment = commentMapper.toEntity(dto);
        if (!dto.hasContent())
            throw new IllegalArgumentException("Comment can't be empty!");

        // check if parent comment provided
        if (dto.parentCommentId() != null) {
            Comment parentComment = commentRepository.findById(dto.parentCommentId())
                    .orElseThrow(() -> new CommentNotFoundException(dto.parentCommentId()));

            // Ensure parent comment belongs to the same post
            if (!parentComment.getPost().getId().equals(postId)) {
                throw new IllegalArgumentException("Parent comment does not belong to this post");
            }

            // Prevent replying to deleted comments
            if (parentComment.isDeleted()) {
                throw new IllegalArgumentException("Cannot reply to deleted comment");
            }

            comment.setParentCommentId(dto.parentCommentId());
        }

        comment.setPost(post);
        comment.setAuthorId(user.getId());
        comment = commentRepository.save(comment);

        post.setCommentsCount(post.getCommentsCount() + 1);
        postRepository.save(post);

        kafkaProducerService.sendRecommendationSentimentUpdated(comment.getPost().getId(), comment.getText());

        return commentMapper.toResponseDto(comment);
    }

    @Transactional(readOnly = true)
    public Page<CommentResponseDTO> getAllByPostId(Long postId, Pageable pageable) {
        postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException(postId));
        Page<Comment> page = commentRepository.findAllByPostId(postId, pageable);
        return page.map(commentMapper::toResponseDto);
    }

    @PreAuthorize("isAuthenticated()")
    @RequireNonLockedUser
    @Transactional
    public CommentResponseDTO updateById(Long commentId, CommentUpdateDTO dto, @AuthenticationPrincipal User user) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException(commentId));

        if (!comment.getAuthorId().equals(user.getId())) {
            throw new AccessDeniedException("You can only update your own comment");
        }

        if (!dto.hasContent()) throw new IllegalArgumentException("Comment can't be empty");

        if (comment.isDeleted()) {
            throw new IllegalArgumentException("Cannot update deleted comment");
        }

        commentMapper.updateFromDto(dto, comment);
        comment = commentRepository.save(comment);
        kafkaProducerService.sendRecommendationSentimentUpdated(comment.getPost().getId(), comment.getText());

        return commentMapper.toResponseDto(comment);
    }

    @PreAuthorize("isAuthenticated()")
    @RequireNonLockedUser
    @Transactional
    public void deleteById(Long commentId, @AuthenticationPrincipal User user) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException(commentId));

        // Check if already deleted
        if (comment.isDeleted()) {
            throw new IllegalArgumentException("Comment is already deleted");
        }

        boolean isAuthor = comment.getAuthorId().equals(user.getId());
        boolean isAdmin = user.getRoles().stream().anyMatch(r -> r.getName() == RoleEnum.ADMIN);
        if (!isAuthor && !isAdmin) {
            throw new AccessDeniedException("You can only delete your own comment");
        }

        comment.setDeletedAt(Instant.now());
        commentRepository.save(comment);
        kafkaProducerService.sendRecommendationSentimentUpdated(comment.getPost().getId(), comment.getText());

        // count only active comments
        Post post = comment.getPost();
        if (post.getCommentsCount() > 0) {
            post.setCommentsCount(post.getCommentsCount() - 1);
            postRepository.save(post);
        }
    }

}