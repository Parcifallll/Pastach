package com.example.Pastach.service;

import com.example.Pastach.dto.mapper.UserMapper;
import com.example.Pastach.dto.user.UserCreateDTO;
import com.example.Pastach.model.*;
import com.example.Pastach.repository.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Transactional
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // do not user H2
@Testcontainers
@DisplayName("Integration tests ReactionService")
class ReactionServiceIntegrationTest {
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16")
            .withDatabaseName("testdb")
            .withUsername("postgres")
            .withPassword("admin");
    @Autowired
    private UserMapper userMapper;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    private static final Logger log = LoggerFactory.getLogger(ReactionServiceIntegrationTest.class);

    @Autowired
    private ReactionService reactionService;

    @Autowired
    private ReactionRepository reactionRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    private User user;
    private Post post;
    private Comment comment;

    @BeforeEach
    void setUp() {
        Role userRole = roleRepository.findByName(RoleEnum.USER)
                .orElseThrow(() -> new IllegalStateException("USER not found"));

        user = new User();
        user.setUsername("username");
        user.setEmail("user@example.com");
        user.setPassword("password");
        user.setFirstName("roman");
        user.setLastName("pisanov");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        user.setRoles(Set.of(userRole));
        user = userRepository.saveAndFlush(user);

        Authentication auth = new UsernamePasswordAuthenticationToken(
                user, null, user.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(auth);


        post = new Post();
        post.setAuthorId(user.getId());
        post.setText("textPost");
        post.setLikesCount(0L);
        post.setDislikesCount(0L);
        postRepository.save(post);

        comment = new Comment();
        comment.setPost(post);
        comment.setAuthorId(user.getId());
        comment.setText("textComment");
        comment.setLikesCount(0L);
        comment.setDislikesCount(0L);
        commentRepository.save(comment);

    }

    // tests for reactions on Post

    @Test
    @DisplayName("user creates reaction on post")
    void success_create_reaction() {
        reactionService.toggleReaction(ReactionTargetType.POST, post.getId(), ReactionType.LIKE, user);

        Post updated = postRepository.findById(post.getId()).get();
        assertThat(updated.getLikesCount()).isEqualTo(1L);
        assertThat(updated.getDislikesCount()).isEqualTo(0L);

        Reaction reaction = reactionRepository.findByTargetTypeAndTargetIdAndAuthorId(
                ReactionTargetType.POST, post.getId(), user.getId()).get();
        assertThat(reaction.getType()).isEqualTo(ReactionType.LIKE);
    }


    @Test
    @DisplayName("user update reaction in post to another")
    void success_update_reaction() {
        reactionService.toggleReaction(ReactionTargetType.POST, post.getId(), ReactionType.LIKE, user);

        reactionService.toggleReaction(ReactionTargetType.POST, post.getId(), ReactionType.DISLIKE, user);
        Post post_dislike = postRepository.findById(post.getId()).get();
        assertThat(post_dislike.getLikesCount()).isEqualTo(0L);
        assertThat(post_dislike.getDislikesCount()).isEqualTo(1L);

        Reaction reaction = reactionRepository.findByTargetTypeAndTargetIdAndAuthorId(
                ReactionTargetType.POST, post.getId(), user.getId()).get();
        assertThat(reaction.getType()).isEqualTo(ReactionType.DISLIKE);
    }

    @Test
    @DisplayName("user delete reaction on post by double add-click")
    void success_delete_reaction() {
        reactionService.toggleReaction(ReactionTargetType.POST, post.getId(), ReactionType.LIKE, user);
        reactionService.toggleReaction(ReactionTargetType.POST, post.getId(), ReactionType.LIKE, user);

        Post updated = postRepository.findById(post.getId()).get();
        assertThat(updated.getLikesCount()).isEqualTo(0L);
        assertThat(updated.getDislikesCount()).isEqualTo(0L);

        assertThat(reactionRepository.findByTargetTypeAndTargetIdAndAuthorId(
                ReactionTargetType.POST, post.getId(), user.getId()))
                .isEmpty();

    }
}