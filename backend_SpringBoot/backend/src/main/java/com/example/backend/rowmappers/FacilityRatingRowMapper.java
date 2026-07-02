package com.example.backend.rowmappers;

import com.example.backend.dtos.RatingResponse;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class FacilityRatingRowMapper implements RowMapper<RatingResponse> {
    @Override
    public RatingResponse mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new RatingResponse(
                rs.getLong("id"),
                rs.getString("username"),
                rs.getBoolean("is_like"),
                rs.getString("comment"),
                rs.getTimestamp("created_at").toLocalDateTime()
        );
    }
}