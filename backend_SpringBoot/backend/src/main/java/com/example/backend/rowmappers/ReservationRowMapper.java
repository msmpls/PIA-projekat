package com.example.backend.rowmappers;

import com.example.backend.models.Reservation;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ReservationRowMapper implements RowMapper<Reservation> {
    @Override
    public Reservation mapRow(ResultSet rs, int rowNum) throws SQLException {
        Reservation r = new Reservation();
        r.setId(rs.getLong("id"));
        r.setUser_id(rs.getLong("user_id"));
        r.setCourt_id(rs.getLong("court_id"));
        r.setReservation_date(rs.getString("reservation_date"));
        r.setStart_time(rs.getString("start_time"));
        r.setEnd_time(rs.getString("end_time"));
        r.setStatus(rs.getString("status"));
        return r;
    }
}