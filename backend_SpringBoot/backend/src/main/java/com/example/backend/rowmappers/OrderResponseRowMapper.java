package com.example.backend.rowmappers;

import com.example.backend.dtos.OrderResponse;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

public class OrderResponseRowMapper implements RowMapper<OrderResponse> {
    @Override
    public OrderResponse mapRow(ResultSet rs, int rowNum) throws SQLException {
        OrderResponse response = new OrderResponse();
        response.setId(rs.getLong("id"));
        response.setOrder_date(rs.getString("order_date"));
        response.setTotal_price(rs.getDouble("total_price"));
        response.setStatus(rs.getString("status"));
        response.setItems_summary(rs.getString("items_summary"));
        response.setTotal_original_price(rs.getDouble("total_original_price"));
        return response;
    }
}