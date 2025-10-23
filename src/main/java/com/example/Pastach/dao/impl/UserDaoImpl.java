package com.example.Pastach.dao.impl;

import com.example.Pastach.dao.UserDao;
import com.example.Pastach.dao.mappers.UserRowMapper;
import com.example.Pastach.exception.UserNotFoundException;
import com.example.Pastach.model.Post;
import com.example.Pastach.model.User;
import com.example.Pastach.service.UserService;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class UserDaoImpl implements UserDao {
    private final Logger log = LoggerFactory.getLogger(UserDaoImpl.class.getName());
    private final JdbcTemplate jdbcTemplate;
    private final UserRowMapper userRowMapper;

    public UserDaoImpl(JdbcTemplate jdbcTemplate, UserRowMapper userRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.userRowMapper = userRowMapper;
    }

    @Override
    public Optional<User> findUserById(String id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        log.info("sql request sent");
        try {
            log.info(jdbcTemplate.getDataSource().toString());
            User user = jdbcTemplate.queryForObject(sql, userRowMapper, id);
            return Optional.of(user);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<User> findAll() {
        String sql = "SELECT * FROM users";
        List<User> users = jdbcTemplate.query(sql, userRowMapper);
        return users;
    }
}
