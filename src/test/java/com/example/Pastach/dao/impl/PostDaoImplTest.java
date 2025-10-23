package com.example.Pastach.dao.impl;

import com.example.Pastach.dao.UserDao;
import com.example.Pastach.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;


@SpringBootTest
@AutoConfigureTestDatabase
@ActiveProfiles("test")
class PostDaoImplTest {
    @Autowired
    private UserDao userDao;

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

}
