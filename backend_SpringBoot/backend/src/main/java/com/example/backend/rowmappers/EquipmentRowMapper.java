package com.example.backend.rowmappers;

import com.example.backend.models.Equipment;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EquipmentRowMapper implements RowMapper<Equipment> {
    @Override
    public Equipment mapRow(ResultSet rs, int rowNum) throws SQLException {
        Equipment eq = new Equipment();
        eq.setId(rs.getLong("id"));
        eq.setName(rs.getString("name"));
        eq.setImage_path(rs.getString("image_path"));
        eq.setSport_type(rs.getString("sport_type"));
        eq.setPrice(rs.getDouble("price"));
        eq.setStock_quantity(rs.getInt("stock_quantity"));
        eq.setDescription(rs.getString("description"));
        return eq;
    }
}