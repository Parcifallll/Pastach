package com.example.Pastach.dao.impl;

import com.example.Pastach.dao.PostDao;
import com.example.Pastach.dao.UserDao;
import com.example.Pastach.model.Post;
import com.example.Pastach.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;


@SpringBootTest
@AutoConfigureTestDatabase
@ActiveProfiles("test")
class PostDaoImplTest {
    @Autowired
    private UserDao userDao;
    Autowired
    private PostDao postDao;
    private Post post;

    @BeforeEach
    void setUp() {
        post = new Post(1, "hehe_boy", "text", "url", LocalDate.now().atStartOfDay());
    }

    @Test
    void findPostsByUser_UserExists_ReturnsUser() {
        Optional<User> user = userDao.findUserById("hehe_boy");

        assertThat(user).isPresent();
        assertThat(user.get().getId()).isEqualTo("hehe_boy");
        assertThat(user.get().getUserName()).isEqualTo("Roman");
    }

    @Test
    void findPostsByUser_UserDoesNotExist_ReturnsEmpty() {
        Optional<User> user = userDao.findUserById("jjj");

        assertThat(user).isEmpty();
    }


//    @Test
//    void findAll() {
//        Collection<Post> posts = postDao.findAll();
//        assertThat(posts).containsExactlyElementsOf();
//
//    }

    @Test
    void searchPosts() {
    }

    @Test
    void create() {
    }

    @Test
    void deleteById() {
    }

    @Test
    void updateById() {
    }
}
