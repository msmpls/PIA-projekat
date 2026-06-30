package com.example.backend.repos;

import com.example.backend.models.Sport;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SportsRepository {

    private final JdbcTemplate jdbcTemplate;

    public SportsRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<String> findAllSportNames() {
        String sql = "SELECT name FROM sports ORDER BY name";
        return jdbcTemplate.queryForList(sql, String.class);
    }

    public boolean existsByName(String name) {
        String sql = "SELECT COUNT(*) FROM sports WHERE name = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, name);
        return count != null && count > 0;
    }

    public int save(Sport sport) {
        String sql = "INSERT INTO sports (name, description) VALUES (?, ?)";
        return jdbcTemplate.update(sql, sport.getName(), sport.getDescription());
    }
}
