package com.example.backend.rowmappers;

import com.example.backend.models.Coach;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CoachRowMapper implements RowMapper<Coach> {
    @Override
    public Coach mapRow(ResultSet rs, int rowNum) throws SQLException {
        Coach coach = new Coach();
        coach.setId(rs.getLong("id"));
        coach.setFacility_id(rs.getLong("facility_id"));
        coach.setName(rs.getString("name"));
        coach.setSpecialization(rs.getString("specialization"));
        coach.setSport(rs.getString("sport"));
        coach.setPrice_per_hour(rs.getDouble("price_per_hour"));
        coach.setAverage_rating(rs.getDouble("average_rating"));
        coach.setIs_active(rs.getBoolean("is_active"));
        return coach;
    }
}