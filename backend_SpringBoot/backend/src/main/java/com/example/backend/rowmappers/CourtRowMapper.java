package com.example.backend.rowmappers;

import com.example.backend.models.Court;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CourtRowMapper implements RowMapper<Court> {
    @Override
    public Court mapRow(ResultSet rs, int rowNum) throws SQLException {
        Court c = new Court();
        c.setId(rs.getLong("id"));
        c.setFacility_id(rs.getLong("facility_id"));
        c.setCourt_name(rs.getString("court_name"));
        c.setSport_type(rs.getString("sport_type"));
        c.setType(rs.getString("type"));
        c.setCapacity(rs.getInt("capacity"));
        c.setEquipment_description(rs.getString("equipment_description"));
        return c;
    }
}