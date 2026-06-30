package com.example.backend.repos;

import com.example.backend.dtos.OrderResponse;
import com.example.backend.models.OrderItem;
import com.example.backend.rowmappers.OrderItemRowMapper;
import com.example.backend.rowmappers.OrderResponseRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class OrderRepository {

    private final JdbcTemplate jdbcTemplate;

    public OrderRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // Kreiranje glavne porudžbine i vraćanje generisanog ID-ja
    public Long createOrder(Long userId, Double totalPrice) {
        String sql = "INSERT INTO orders (user_id, order_date, total_price, status) VALUES (?, NOW(), ?, 'ORDERED')";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, userId);
            ps.setDouble(2, totalPrice);
            return ps;
        }, keyHolder);

        return keyHolder.getKey() != null ? keyHolder.getKey().longValue() : null;
    }

    // Upis pojedinačne stavke u korpu
    public void saveOrderItem(Long orderId, Long equipmentId, Integer quantity,
            double unitPrice, int discount, double finalPrice) {
        String sql = "INSERT INTO order_items (order_id, equipment_id, quantity, unit_price, discount_percent, final_price) "
                +
                "VALUES (?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, orderId, equipmentId, quantity, unitPrice, discount, finalPrice);
    }


    // Otkazivanje porudžbine od strane sportiste
    public int updateStatus(Long orderId, Long userId, String newStatus) {
        String sql = "UPDATE orders SET status = ? WHERE id = ? AND user_id = ?";
        return jdbcTemplate.update(sql, newStatus, orderId, userId);
    }

    // Pomoćna metoda za proveru vlasništva i statusa pre otkazivanja
    public String findStatusByIdAndUserId(Long orderId, Long userId) {
        String sql = "SELECT status FROM orders WHERE id = ? AND user_id = ?";
        List<String> statuses = jdbcTemplate.query(sql, (rs, rowNum) -> rs.getString("status"), orderId, userId);
        return statuses.stream().findFirst().orElse(null);
    }

    public List<OrderResponse> findAllByUserId(Long userId) {
    String sql = "SELECT o.id, o.order_date, o.total_price, o.status, " +
                 "GROUP_CONCAT(CONCAT(e.name, ' (x', oi.quantity, ')') SEPARATOR ', ') AS items_summary, " +
                 "SUM(CAST(oi.unit_price AS DECIMAL(10,2)) * oi.quantity) AS total_original_price " + // Računamo sumu originalnih cena
                 "FROM orders o " +
                 "JOIN order_items oi ON o.id = oi.order_id " +
                 "JOIN equipment e ON oi.equipment_id = e.id " +
                 "WHERE o.user_id = ? " +
                 "GROUP BY o.id " +
                 "ORDER BY o.order_date DESC";
    
    // Potrebno je da ažuriraš OrderResponseRowMapper da mapira total_original_price
    return jdbcTemplate.query(sql, new OrderResponseRowMapper(), userId);
}

    public List<OrderItem> findItemsByOrderId(Long orderId) {
    String sql = "SELECT * FROM order_items WHERE order_id = ?";
    return jdbcTemplate.query(sql, new OrderItemRowMapper(), orderId);
}
}