package com.example.backend.rowmappers;

import com.example.backend.dtos.ReservationResponse;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ReservationResponseRowMapper implements RowMapper<ReservationResponse> {
    @Override
    public ReservationResponse mapRow(ResultSet rs, int rowNum) throws SQLException {
        ReservationResponse response = new ReservationResponse();
        
        response.setId(rs.getLong("id"));
        response.setUser_id(rs.getLong("user_id"));
        response.setReservation_date(rs.getString("reservation_date"));
        response.setStart_time(rs.getString("start_time"));
        response.setEnd_time(rs.getString("end_time"));
        response.setStatus(rs.getString("status"));
        
        // Podaci dobijeni preko SQL JOIN-a sa tabelama 'facilities' i 'courts'
        response.setFacility_name(rs.getString("facility_name"));
        response.setCity(rs.getString("city"));
        response.setCourt_name_number(rs.getString("court_name_number"));
        response.setSport(rs.getString("sport"));
        
        return response;
    }
}