package com.example.Pastach.dao.impl;

import com.example.Pastach.dao.PostDao;
import com.example.Pastach.dao.mappers.PostRowMapper;
import com.example.Pastach.model.Post;
import com.example.Pastach.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Component
public class PostDaoImpl implements PostDao {
    private final JdbcTemplate jdbcTemplate;
    private final PostRowMapper postRowMapper;
    private final Logger log = LoggerFactory.getLogger(PostDaoImpl.class);

    public PostDaoImpl(JdbcTemplate jdbcTemplate, PostRowMapper postRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.postRowMapper = postRowMapper;
    }

    @Override
    public Collection<Post> findPostsByUser(User user) {
        String sql = "select * from posts where author_id = ? order by creation_date";
        List<Post> posts = jdbcTemplate.query(sql, postRowMapper, user.getId());
        return posts;
    }
}
