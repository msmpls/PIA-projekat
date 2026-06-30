package com.example.backend.rowmappers;

import com.example.backend.dtos.TrainingResponse;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TrainingResponseRowMapper implements RowMapper<TrainingResponse> {
    @Override
    public TrainingResponse mapRow(ResultSet rs, int rowNum) throws SQLException {
        TrainingResponse response = new TrainingResponse();
        response.setId(rs.getLong("id"));
        response.setCoach_name(rs.getString("coach_name"));
        response.setFacility_name(rs.getString("facility_name"));
        response.setCourt_id(rs.getLong("court_id"));
        response.setCourt_name(rs.getString("court_name"));
        response.setSport(rs.getString("sport"));
        response.setTraining_date(rs.getString("training_date"));
        response.setStart_time(rs.getString("start_time"));
        response.setEnd_time(rs.getString("end_time"));
        response.setStatus(rs.getString("status"));
        return response;
    }
}