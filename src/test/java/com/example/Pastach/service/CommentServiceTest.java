package com.example.Pastach.service;

import com.example.Pastach.dto.comment.CommentCreateDTO;
import com.example.Pastach.dto.comment.CommentResponseDTO;
import com.example.Pastach.dto.comment.CommentUpdateDTO;
import com.example.Pastach.dto.mapper.CommentMapper;
import com.example.Pastach.model.*;
import com.example.Pastach.repository.CommentRepository;
import com.example.Pastach.repository.PostRepository;
import com.example.Pastach.repository.RoleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {
    @Mock
    private CommentRepository commentRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private CommentMapper commentMapper;

    @InjectMocks
    private CommentService commentService; // a real object!

    @Test
    @DisplayName("Non-empty comment")
    void create_success() {
        // Arrange
        User user = mock(User.class); // mock @AuthPrincipal User
        Long authorId = "userId"; // comment author
        when(user.getId()).thenReturn(authorId);

        Post post = new Post(); // commentsCount = 0
        Long postId = 1L;
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        CommentCreateDTO createDTO = CommentCreateDTO.builder().text("not empty").build();
        Comment comment = new Comment();
        when(commentMapper.toEntity(createDTO)).thenReturn(comment); // for mocked mapper
        when(commentRepository.save(comment)).thenReturn(comment);

        //Act
        CommentResponseDTO responseDTO = commentService.create(postId, createDTO, user);

        //Assert
        // only setters, varify mocks before DB save()
        verify(commentRepository).save(comment); // mock
        verify(commentMapper).toEntity(createDTO);
        assertThat(comment.getAuthorId()).isEqualTo(authorId);
        assertThat(comment.getPost()).isEqualTo(post);

        verify(postRepository).save(post);
        assertThat(post.getCommentsCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("Empty comment, counter incremented")
    void create_fails_when_empty_comment() {
        Long postId = 1L;
        Post post = new Post();
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        User user = mock(User.class);

        Comment comment = new Comment();
        CommentCreateDTO createDTO = CommentCreateDTO.builder().build();
        when(commentMapper.toEntity(createDTO)).thenReturn(comment);


        assertThatThrownBy(() -> commentService.create(postId, createDTO, user))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Comment can't be empty!");
        verify(commentRepository, never()).save(comment);
        assertThat(post.getCommentsCount()).isEqualTo(0);
    }


    @Test
    @DisplayName("updated successfully by user")
    void update_success() {
        User user = mock(User.class);
        String userId = "userId";
        when(user.getId()).thenReturn(userId);

        Post post = new Post();
        post.setCommentsCount(1);

        Comment comment = new Comment();
        comment.setAuthorId(userId);
        comment.setPost(post);
        Long commentId = 1L;
        CommentUpdateDTO commentUpdateDTO = CommentUpdateDTO.builder().text("new text").build();
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        when(commentRepository.save(comment)).thenReturn(comment);

        // Act
        CommentResponseDTO commentResponseDTO = commentService.updateById(commentId, commentUpdateDTO, user);

        // Assert
        verify(commentMapper).updateFromDto(commentUpdateDTO, comment);
        verify(commentRepository).save(comment);
        assertThat(post.getCommentsCount()).isEqualTo(1);

    }

    @Test
    @DisplayName("update failed by admin")
    void update_fail_by_admin() {
        User admin = mock(User.class);
        Role adminRole = new Role();
        adminRole.setName(RoleEnum.ADMIN);
        // when(admin.getRoles()).thenReturn(Set.of(adminRole));
        String adminId = "adminId";
        when(admin.getId()).thenReturn(adminId);

        Post post = new Post();
        post.setCommentsCount(1);

        Comment comment = new Comment();
        comment.setAuthorId("userId");
        comment.setPost(post);
        Long commentId = 1L;
        CommentUpdateDTO commentUpdateDTO = CommentUpdateDTO.builder().text("new text").build();
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        // when(commentRepository.save(comment)).thenReturn(comment);

        assertThatThrownBy(() -> commentService.updateById(commentId, commentUpdateDTO, admin))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("You can only update your own comment");
        verify(commentMapper, never()).updateFromDto(commentUpdateDTO, comment);
        verify(commentRepository, never()).save(comment);
        assertThat(post.getCommentsCount()).isEqualTo(1);

    }

    @Test
    @DisplayName("do not update comment and counter (no access to comment of other owner)")
    void update_fails_when_not_owner() {
        User user = mock(User.class);
        String userId = "userId"; // not author
        when(user.getId()).thenReturn(userId);

        Comment comment = new Comment();
        Long commentId = 1L;
        comment.setAuthorId("author");
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        CommentUpdateDTO commentUpdateDTO = CommentUpdateDTO.builder().text("text").build();

        assertThatThrownBy(() -> commentService.updateById(commentId, commentUpdateDTO, user))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("You can only update your own comment");

        verify(commentMapper, never()).updateFromDto(commentUpdateDTO, comment); // or updateFromDto(any(), any())
    }

    @Test
    @DisplayName("user success delete own comment")
    void delete_success() {
        Post post = new Post();
        post.setCommentsCount(1);

        User user = mock(User.class);
        String userId = "userId";
        when(user.getId()).thenReturn(userId);

        Comment comment = new Comment();
        Long commentId = 1L;
        comment.setPost(post);
        comment.setAuthorId(userId);
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        commentService.deleteById(commentId, user);

        verify(commentRepository).deleteById(commentId);
        assertThat(post.getCommentsCount()).isEqualTo(0);
    }

    @Test
    @DisplayName("user fail delete other user's comment")
    void delete_fail_other_comment() {
        Post post = new Post();
        post.setCommentsCount(1);

        User user = mock(User.class);
        String userId = "userId";
        when(user.getId()).thenReturn(userId);

        Comment comment = new Comment();
        Long commentId = 1L;
        comment.setPost(post);
        comment.setAuthorId("authorId");
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        assertThatThrownBy(() -> commentService.deleteById(commentId, user))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("You can only delete your own comment");
        verify(commentRepository, never()).deleteById(commentId);
        assertThat(post.getCommentsCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("admin success delete other post")
    void delete_success_admin_other_post() {
        Post post = new Post();
        post.setCommentsCount(1);

        User admin = mock(User.class);
        Role adminRole = new Role();
        adminRole.setName(RoleEnum.ADMIN);
        when(admin.getRoles()).thenReturn(Set.of(adminRole));
        String adminId = "adminId";
        when(admin.getId()).thenReturn(adminId);

        Comment comment = new Comment();
        Long commentId = 1L;
        comment.setPost(post);
        comment.setAuthorId("authorId");
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        commentService.deleteById(commentId, admin);

        verify(commentRepository).deleteById(commentId);
        assertThat(post.getCommentsCount()).isEqualTo(0);
    }
}