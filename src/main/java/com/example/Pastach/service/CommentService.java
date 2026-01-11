package com.example.Pastach.service;

import com.example.Pastach.dto.comment.CommentCreateDTO;
import com.example.Pastach.dto.comment.CommentResponseDTO;
import com.example.Pastach.dto.comment.CommentUpdateDTO;
import com.example.Pastach.dto.mapper.CommentMapper;
import com.example.Pastach.exception.CommentNotFoundException;
import com.example.Pastach.exception.PostNotFoundException;
import com.example.Pastach.model.Comment;
import com.example.Pastach.model.Post;
import com.example.Pastach.model.RoleEnum;
import com.example.Pastach.model.User;
import com.example.Pastach.repository.CommentRepository;
import com.example.Pastach.repository.PostRepository;
import com.example.Pastach.security.RequireNonLockedUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final CommentMapper commentMapper;


    @PreAuthorize("isAuthenticated()")
    @RequireNonLockedUser
    @Transactional
    public CommentResponseDTO create(Long postId, CommentCreateDTO dto, @AuthenticationPrincipal User user) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException(postId));

        Comment comment = commentMapper.toEntity(dto);
        if (!dto.hasContent())
            throw new IllegalArgumentException("Comment can't be empty!");
        comment.setPost(post);
        comment.setAuthorId(user.getId());
        comment = commentRepository.save(comment);

        post.setCommentsCount(post.getCommentsCount() + 1);
        postRepository.save(post);

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

        if(!dto.hasContent()) throw new IllegalArgumentException("Comment can't be empty");
        if (!comment.getAuthorId().equals(user.getId())) {
            throw new AccessDeniedException("You can only update your own comment");
        }

        commentMapper.updateFromDto(dto, comment);
        comment = commentRepository.save(comment);

        return commentMapper.toResponseDto(comment);
    }

    @PreAuthorize("isAuthenticated()")
    @RequireNonLockedUser
    @Transactional
    public void deleteById(Long commentId, @AuthenticationPrincipal User user) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new CommentNotFoundException(commentId));

        boolean isAuthor = comment.getAuthorId().equals(user.getId());
        boolean isAdmin = user.getRoles().stream().anyMatch(r -> r.getName() == RoleEnum.ADMIN);
        if (!isAuthor && !isAdmin) {
            throw new AccessDeniedException("You can only delete your own comment");
        }

        Post post = comment.getPost();
        if (post.getCommentsCount() > 0) {
            post.setCommentsCount(post.getCommentsCount() - 1);
            postRepository.save(post);
        }

        commentRepository.deleteById(commentId);
    }

}
