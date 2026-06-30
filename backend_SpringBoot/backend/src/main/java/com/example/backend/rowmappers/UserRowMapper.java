package com.example.backend.rowmappers;

import com.example.backend.models.User;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserRowMapper implements RowMapper<User> {

    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setUsername(rs.getString("username"));
        user.setPassword_hash(rs.getString("password_hash"));
        user.setEmail(rs.getString("email"));
        user.setFirst_name(rs.getString("first_name"));
        user.setLast_name(rs.getString("last_name"));
        user.setPhone(rs.getString("phone"));
        user.setRole(rs.getString("role"));
        user.setStatus(rs.getString("status"));
        user.setProfile_picture_path(rs.getString("profile_picture_path"));
        user.setFavorite_sports(rs.getString("favorite_sports"));
        return user;
    }
}