package com.example.backend.rowmappers;

import com.example.backend.models.Order;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

public class OrderRowMapper implements RowMapper<Order> {
    @Override
    public Order mapRow(ResultSet rs, int rowNum) throws SQLException {
        Order o = new Order();
        o.setId(rs.getLong("id"));
        o.setUser_id(rs.getLong("user_id"));
        o.setOrder_date(rs.getString("order_date"));
        o.setTotal_price(rs.getDouble("total_price"));
        o.setStatus(rs.getString("status"));
        return o;
    }
}
