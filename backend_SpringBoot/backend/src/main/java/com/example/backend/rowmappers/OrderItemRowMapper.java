package com.example.backend.rowmappers;

import com.example.backend.models.OrderItem;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

public class OrderItemRowMapper implements RowMapper<OrderItem> {

    @Override
    public OrderItem mapRow(ResultSet rs, int rowNum) throws SQLException {
        OrderItem item = new OrderItem();
        
        // Osnovni podaci
        item.setId(rs.getLong("id"));
        item.setOrder_id(rs.getLong("order_id"));
        item.setEquipment_id(rs.getLong("equipment_id"));
        item.setQuantity(rs.getInt("quantity"));
        
        // Podaci o cenama (istorijski zapis)
        item.setUnit_price(rs.getDouble("unit_price"));
        item.setDiscount_percent(rs.getInt("discount_percent"));
        item.setFinal_price(rs.getDouble("final_price"));
        
        return item;
    }
}