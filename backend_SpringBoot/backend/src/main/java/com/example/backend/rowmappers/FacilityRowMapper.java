package com.example.backend.rowmappers;

import com.example.backend.models.Facility;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FacilityRowMapper implements RowMapper<Facility> {
    @Override
    public Facility mapRow(ResultSet rs, int rowNum) throws SQLException {
        Facility f = new Facility();
        f.setId(rs.getLong("id"));
        f.setName(rs.getString("name"));
        f.setCity(rs.getString("city"));
        f.setAddress(rs.getString("address"));
        f.setDescription(rs.getString("description"));
        f.setPrice_per_hour(rs.getBigDecimal("price_per_hour"));
        f.setType(rs.getString("type"));
        f.setStatus(rs.getString("status"));
        f.setCreated_by(rs.getLong("created_by"));
        f.setCompany_name(rs.getString("company_name"));
        f.setCompany_address(rs.getString("company_address"));
        f.setCompany_id_number(rs.getString("company_id_number"));
        f.setPib(rs.getString("pib"));
        f.setWork_start_time(rs.getString("work_start_time"));
        f.setWork_end_time(rs.getString("work_end_time"));
        return f;
    }
}