package com.example.backend.repos;

import com.example.backend.dtos.OrderResponse;
import com.example.backend.rowmappers.OrderResponseRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class StaffOrderRepository {

    private final JdbcTemplate jdbcTemplate;

    public StaffOrderRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // Dobavi sve narudžbine za zaposlenog
  public List<OrderResponse> findAllOrders() {
    String sql = "SELECT o.id, o.order_date, o.total_price, o.status, " +
                 "GROUP_CONCAT(CONCAT(e.name, ' (x', oi.quantity, ')') SEPARATOR ', ') AS items_summary, " +
                 "SUM(CAST(oi.unit_price AS DECIMAL(10,2)) * oi.quantity) AS total_original_price " +
                 "FROM orders o " +
                 "JOIN order_items oi ON o.id = oi.order_id " +
                 "JOIN equipment e ON oi.equipment_id = e.id " +
                 "GROUP BY o.id " +
                 "ORDER BY o.order_date DESC";
    return jdbcTemplate.query(sql, new OrderResponseRowMapper());
}

    // Direktna izmena statusa (bez provere vlasnika)
    public void updateStatusDirectly(Long orderId, String newStatus) {
        String sql = "UPDATE orders SET status = ? WHERE id = ?";
        jdbcTemplate.update(sql, newStatus, orderId);
    }
}