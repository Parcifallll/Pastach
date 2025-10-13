package com.example.Pastach.dao.impl;

import com.example.Pastach.dao.UserDao;
import com.example.Pastach.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserDaoImpl implements UserDao {
    private final Logger log = LoggerFactory.getLogger(UserDaoImpl.class);
    private final JdbcTemplate jdbcTemplate;

    public UserDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<User> findUserById(String id) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from users where id = ?", id);
        if (userRows.next()) {
            log.info("Found user: {} {}", userRows.getString("id"), userRows.getString("username"));
            User user = new User();
            user.setId(id);
            // iterator
        } else {
            log.info("No user found with id {}", id);
            // Return empty if no user is found
        }
        // here should be a realization
        return Optional.empty();
    }
}
