package com.example.backend.repos;

import com.example.backend.models.Promotion;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PromotionRepository {

    private final JdbcTemplate jdbcTemplate;

    public PromotionRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Promotion> findAll() {
        String sql = "SELECT * FROM promotions ORDER BY start_date DESC";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Promotion(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getInt("discount_percent"),
                rs.getString("sport_type"),
                rs.getDate("start_date").toLocalDate(),
                rs.getDate("end_date").toLocalDate(),
                rs.getBoolean("is_active")
        ));
    }

    public void save(Promotion p) {
        String sql = "INSERT INTO promotions (name, discount_percent, sport_type, start_date, end_date, is_active) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, p.getName(), p.getDiscount_percent(), p.getSport_type(),
                p.getStart_date(), p.getEnd_date(), p.getIs_active() != null ? p.getIs_active() : true);
    }

    public void update(Long id, Promotion p) {
        String sql = "UPDATE promotions SET name = ?, discount_percent = ?, sport_type = ?, start_date = ?, end_date = ?, is_active = ? WHERE id = ?";
        jdbcTemplate.update(sql, p.getName(), p.getDiscount_percent(), p.getSport_type(),
                p.getStart_date(), p.getEnd_date(), p.getIs_active(), id);
    }

    public void delete(Long id) {
        jdbcTemplate.update("DELETE FROM promotions WHERE id = ?", id);
    }
}
