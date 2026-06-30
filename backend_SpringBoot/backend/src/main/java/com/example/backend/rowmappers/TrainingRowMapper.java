package com.example.backend.rowmappers;

import com.example.backend.models.Training;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TrainingRowMapper implements RowMapper<Training> {
    @Override
    public Training mapRow(ResultSet rs, int rowNum) throws SQLException {
        Training training = new Training();
        training.setId(rs.getLong("id"));
        training.setUser_id(rs.getLong("user_id"));
        training.setCoach_id(rs.getLong("coach_id"));
        training.setCourt_id(rs.getLong("court_id"));
        training.setTraining_date(rs.getString("training_date"));
        training.setStart_time(rs.getString("start_time"));
        training.setEnd_time(rs.getString("end_time"));
        training.setStatus(rs.getString("status"));
        return training;
    }
}