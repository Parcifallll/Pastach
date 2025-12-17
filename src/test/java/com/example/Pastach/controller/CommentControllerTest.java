package com.example.Pastach.controller;

import com.example.Pastach.security.SecurityConfig;
import com.example.Pastach.dto.comment.CommentCreateDTO;
import com.example.Pastach.dto.comment.CommentResponseDTO;
import com.example.Pastach.dto.comment.CommentUpdateDTO;
import com.example.Pastach.exception.PostNotFoundException;
import com.example.Pastach.model.ReactionTargetType;
import com.example.Pastach.model.Role;
import com.example.Pastach.model.RoleEnum;
import com.example.Pastach.model.User;
import com.example.Pastach.service.CommentService;
import com.example.Pastach.security.JwtService;
import com.example.Pastach.service.ReactionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.example.Pastach.model.RoleEnum.ADMIN;
import static org.hamcrest.core.StringContains.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CommentController.class)
@Import(SecurityConfig.class)
class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @MockitoBean
    private CommentService commentService;

    @MockitoBean
    private ReactionService reactionService;

    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    private User createUser(String id, RoleEnum... roles) {
        User user = new User();
        user.setId(id);
        user.setEmail(id + "@example.com");

        var roleSet = roles.length == 0
                ? Set.of(new Role(RoleEnum.USER))
                : Arrays.stream(roles)
                .map(r -> {
                    Role role = new Role();
                    role.setName(r);
                    return role;
                })
                .collect(Collectors.toSet());

        user.setRoles(roleSet);
        return user;
    }

    private void authenticate(User user) {
        Authentication auth = new UsernamePasswordAuthenticationToken(
                user, null, user.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    @DisplayName("POST /posts/1/comments — 200: create comment")
    void create_success() throws Exception {
        User user = createUser("user123");
        authenticate(user);

        CommentCreateDTO dto = CommentCreateDTO.builder().text("Отличный пост!").build();
        CommentResponseDTO response = CommentResponseDTO.builder()
                .id(100L)
                .text("text")
                .authorId("user123")
                .postId(1L)
                .build();

        when(commentService.create(eq(1L), any(), eq(user))).thenReturn(response);

        mockMvc.perform(post("/posts/1/comments")
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.text").value("text"))
                .andExpect(jsonPath("$.authorId").value("user123"));
    }

    @Test
    @DisplayName("POST /posts/1/comments — 400: empty comment")
    void create_empty_comment_bad_request() throws Exception {
        User user = createUser("user123");
        authenticate(user);

        CommentCreateDTO empty = CommentCreateDTO.builder().build();

        when(commentService.create(eq(1L), any(), eq(user)))
                .thenThrow(new IllegalArgumentException("Comment can't be empty!"));

        mockMvc.perform(post("/posts/1/comments")
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(empty)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("Bad request"))
                .andExpect(jsonPath("$.message").value("Comment can't be empty!"));
    }

    @Test
    @DisplayName("POST /posts/1/comments — 401: not authed")
    void create_unauthorized() throws Exception {

        mockMvc.perform(post("/posts/1/comments")
                        .contentType(APPLICATION_JSON)
                        .content("{\"text\":\"text\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("PATCH /posts/1/comments/100 — 200: update own comment")
    void update_success() throws Exception {
        User user = createUser("user123");
        authenticate(user);

        CommentUpdateDTO dto = CommentUpdateDTO.builder().text("new").build();
        CommentResponseDTO response = CommentResponseDTO.builder()
                .id(100L)
                .text("new")
                .authorId("user123")
                .postId(1L)
                .build();

        when(commentService.updateById(eq(100L), any(), eq(user))).thenReturn(response);

        mockMvc.perform(patch("/posts/1/comments/100")
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("new"));
    }

    @Test
    @DisplayName("PATCH /posts/1/comments/100 — 403: no access")
    void update_forbidden_foreign_comment() throws Exception {
        User user = createUser("user123");
        authenticate(user);

        when(commentService.updateById(eq(100L), any(), eq(user)))
                .thenThrow(new AccessDeniedException("You can only update your own comment"));

        mockMvc.perform(patch("/posts/1/comments/100")
                        .contentType(APPLICATION_JSON)
                        .content("{\"text\":\"hack\"}"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.type").value("Forbidden"))
                .andExpect(jsonPath("$.message").value("You can only update your own comment"));
    }

    @Test
    @DisplayName("DELETE /posts/1/comments/100 — 204: delete own comment")
    void delete_success() throws Exception {
        User user = createUser("user123");
        authenticate(user);

        doNothing().when(commentService).deleteById(eq(100L), eq(user));

        mockMvc.perform(delete("/posts/1/comments/100"))
                .andExpect(status().isNoContent());
    }


    @Test
    @DisplayName("DELETE /posts/1/comments/100 — 204: delete by admin other comment")
    void delete_success_admin() throws Exception {
        User user = createUser("user123", ADMIN);
        authenticate(user);

        doNothing().when(commentService).deleteById(eq(100L), eq(user));

        mockMvc.perform(delete("/posts/1/comments/100"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /posts/1/comments/100 — 403: no access to delete")
    void delete_forbidden_foreign_comment() throws Exception {
        User user = createUser("user123");
        authenticate(user);

        doThrow(new AccessDeniedException("You can only delete your own comment"))
                .when(commentService).deleteById(eq(100L), eq(user));

        mockMvc.perform(delete("/posts/1/comments/100"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("You can only delete your own comment"));
    }

    @Test
    @DisplayName("PUT /posts/1/comments/100/reactions — 200: react")
    void react_to_comment_success() throws Exception {
        User user = createUser("user123");
        authenticate(user);

        doNothing().when(reactionService)
                .toggleReaction(eq(ReactionTargetType.COMMENT), eq(100L), any(), eq(user));

        mockMvc.perform(put("/posts/1/comments/100/reactions")
                        .contentType(APPLICATION_JSON)
                        .content("{\"type\":\"LIKE\"}"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /posts/1/comments — 404: no post")
    void get_all_comments_post_not_found() throws Exception {
//        User user = createUser("user123");
//        authenticate(user);

        when(commentService.getAllByPostId(eq(999L), any()))
                .thenThrow(new PostNotFoundException(999L));

        mockMvc.perform(get("/posts/999/comments"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.type").value("Not found"));
    }

    @Test
    @WithMockUser(username = "user123")
    @DisplayName("GET /posts/1/comments — 200: comment page")
    void getAllByPostId_success() throws Exception {
        CommentResponseDTO comment1 = CommentResponseDTO.builder()
                .id(100L)
                .text("1")
                .authorId("user123")
                .postId(1L)
                .build();

        CommentResponseDTO comment2 = CommentResponseDTO.builder()
                .id(101L)
                .text("2")
                .authorId("user456")
                .postId(1L)
                .build();

        Page<CommentResponseDTO> page = new PageImpl<>(
                List.of(comment1, comment2),
                PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt")),
                2
        );

        when(commentService.getAllByPostId(eq(1L), any(Pageable.class)))
                .thenReturn(page);

        mockMvc.perform(get("/posts/1/comments")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "createdAt,desc"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", containsString("application/hal+json")))

                .andExpect(jsonPath("$._embedded.commentResponseDTOList").isArray())
                .andExpect(jsonPath("$._embedded.commentResponseDTOList").isNotEmpty())
                .andExpect(jsonPath("$._embedded.commentResponseDTOList[0].id").value(100))
                .andExpect(jsonPath("$._embedded.commentResponseDTOList[0].text").value("1"))
                .andExpect(jsonPath("$._embedded.commentResponseDTOList[1].id").value(101))

                .andExpect(jsonPath("$.page.size").value(10))
                .andExpect(jsonPath("$.page.totalElements").value(2))
                .andExpect(jsonPath("$.page.totalPages").value(1))
                .andExpect(jsonPath("$.page.number").value(0))

                .andExpect(jsonPath("$._links.self.href").value(containsString("/posts/1/comments")));
    }

}