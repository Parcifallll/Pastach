package com.example.Pastach.dao.mappers;

import com.example.Pastach.model.Post;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

@Component
public class PostRowMapper implements RowMapper<Post> {
    @Override
    public Post mapRow(ResultSet rs, int rowNum) throws SQLException {
        int id = rs.getInt("id");
        String author = rs.getString("author_id");
        String text = rs.getString("text");
        String photoUrl = rs.getString("photo_url");
        LocalDateTime creationDate = rs.getTimestamp("creation_date").toLocalDateTime();

        Post post = new Post(id, author, text, photoUrl, creationDate);
        return post;
    }
}
