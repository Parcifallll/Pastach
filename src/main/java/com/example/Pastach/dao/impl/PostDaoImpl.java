package com.example.Pastach.dao.impl;

import com.example.Pastach.dao.PostDao;
import com.example.Pastach.dao.mappers.PostRowMapper;
import com.example.Pastach.model.Post;
import com.example.Pastach.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Map;
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
    public Collection<Post> findPostsByUser(String userId) {
        String sql = "select * from posts where author_id = ? ORDER BY creation_date DESC";
        List<Post> posts = jdbcTemplate.query(sql, postRowMapper, userId);
        return posts;
    }

    @Override
    public Collection<Post> findAll() {
        String sql = "select * from posts ORDER BY creation_date DESC";
        List<Post> posts = jdbcTemplate.query(sql, postRowMapper);
        return posts;
    }

    @Override
    public Collection<Post> searchPosts(String author, LocalDate creationDate) {
        String sql = "select * from posts where author_id = ? AND creation_date = ? order by creation_date";
        List<Post> posts = jdbcTemplate.query(sql, postRowMapper, author, creationDate);
        return posts;
    }

    @Override
    public Post create(Post post) {
        String sql = "INSERT INTO posts(author_id, text, post_date) VALUES (?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, post.getAuthor());
            ps.setString(2, post.getText());
            ps.setTimestamp(3, Timestamp.from(Instant.from(post.getCreationDate())));
            return ps;
        }, keyHolder);

        post.setId(keyHolder.getKey().intValue());
        return post;
    }

    @Override
    public Optional<Post> deleteById(int postId) {
        String selectSql = "SELECT * FROM posts WHERE id = ?";

        Optional<Post> post = jdbcTemplate.query(selectSql, postRowMapper, postId)
                .stream()
                .findFirst();

        if (post.isPresent()) {
            String deleteSql = "DELETE FROM posts WHERE id = ?";
            int rowsAffected = jdbcTemplate.update(deleteSql, postId);

            if (rowsAffected > 0) {
                return post;
            }
        }

        return Optional.empty();
    }

    @Override
    public Post updateById(Post post, int postId) {
        String sql = "UPDATE posts SET author_id = ?, text = ?, photo_url = ?, creation_date = ? WHERE id = ?";

        int rowsAffected = jdbcTemplate.update(sql,
                post.getAuthor(),
                post.getText(),
                post.getPhotoUrl(),
                post.getCreationDate(),
                postId
        );

        if (rowsAffected == 0) {
            throw new RuntimeException("Post with id " + postId + " not found for update");
        }

        post.setId(postId);
        return post;
    }


}
