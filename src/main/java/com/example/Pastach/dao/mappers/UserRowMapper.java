package com.example.Pastach.dao.mappers;

import com.example.Pastach.model.User;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

@Component
public class UserRowMapper implements RowMapper<User> {

    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
        String id = rs.getString("id");
        String username = rs.getString("username");
        String email = rs.getString("email");

        LocalDate birthday = null;
        java.sql.Date sqlDate = rs.getDate("birthday");
        if (sqlDate != null) {
            birthday = sqlDate.toLocalDate();
        }

        User user = new User(id, username, email, birthday);
        return user;
    }
}