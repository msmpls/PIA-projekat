package com.example.backend.services;

import com.example.backend.dtos.StatisticsResponse;
import com.example.backend.models.User;
import com.example.backend.repos.UserRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class StatisticsService {

    private final JdbcTemplate jdbcTemplate;
    private final UserRepository userRepository;

    public StatisticsService(JdbcTemplate jdbcTemplate, UserRepository userRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.userRepository = userRepository;
    }

    public StatisticsResponse getMyStatistics(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Korisnik nije pronađen."));

        // 1. Bar chart: broj rezervacija po sportu
        String sportSql = "SELECT c.sport_type AS sport, COUNT(*) AS count " +
                "FROM reservations r " +
                "JOIN courts c ON r.court_id = c.id " +
                "WHERE r.user_id = ? AND r.status IN ('CONFIRMED','COMPLETED','PENDING') " +
                "GROUP BY c.sport_type " +
                "ORDER BY count DESC";
        List<Map<String, Object>> reservationsBySport = jdbcTemplate.queryForList(sportSql, user.getId());

        // 2. Line chart: mesecna aktivnost (rezervacije + treninzi po mesecu)
        String monthlySql = "SELECT month_label, SUM(cnt) AS count FROM (" +
                "  SELECT DATE_FORMAT(r.reservation_date, '%Y-%m') AS month_label, COUNT(*) AS cnt " +
                "  FROM reservations r WHERE r.user_id = ? AND r.status != 'CANCELLED' " +
                "  GROUP BY DATE_FORMAT(r.reservation_date, '%Y-%m') " +
                "  UNION ALL " +
                "  SELECT DATE_FORMAT(t.training_date, '%Y-%m') AS month_label, COUNT(*) AS cnt " +
                "  FROM trainings t WHERE t.user_id = ? AND t.status != 'CANCELLED' " +
                "  GROUP BY DATE_FORMAT(t.training_date, '%Y-%m') " +
                ") combined " +
                "GROUP BY month_label " +
                "ORDER BY month_label ASC";
        List<Map<String, Object>> monthlyActivity = jdbcTemplate.queryForList(monthlySql, user.getId(), user.getId());

        // 3. Ukupna potrosnja na opremu (sve porudzbine koje nisu otkazane)
        String spendingSql = "SELECT COALESCE(SUM(total_price), 0) AS total " +
                "FROM orders WHERE user_id = ? AND status != 'CANCELLED'";
        Double totalSpending = jdbcTemplate.queryForObject(spendingSql, Double.class, user.getId());
        if (totalSpending == null) totalSpending = 0.0;

        return new StatisticsResponse(reservationsBySport, monthlyActivity, totalSpending);
    }
}
