package com.example.backend.repos;

import com.example.backend.models.Coach;
import com.example.backend.models.Facility;
import com.example.backend.models.User;
import com.example.backend.rowmappers.CoachRowMapper;
import com.example.backend.rowmappers.FacilityRowMapper;
import com.example.backend.rowmappers.UserRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class AdminRepository {

    private final JdbcTemplate jdbcTemplate;

    public AdminRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<User> findAllUsers() {
        String sql = "SELECT * FROM users ORDER BY status DESC, username";
        return jdbcTemplate.query(sql, new UserRowMapper());
    }

    public Optional<User> findUserById(Long userId) {
        String sql = "SELECT * FROM users WHERE id = ?";
        List<User> users = jdbcTemplate.query(sql, new UserRowMapper(), userId);
        return users.stream().findFirst();
    }

    public int updateUserStatus(Long userId, String status) {
        String sql = "UPDATE users SET status = ? WHERE id = ?";
        return jdbcTemplate.update(sql, status, userId);
    }

    public int deleteUserById(Long userId) {
        String sql = "DELETE FROM users WHERE id = ?";
        return jdbcTemplate.update(sql, userId);
    }

    public Optional<Facility> findFacilityById(Long facilityId) {
        String sql = "SELECT * FROM facilities WHERE id = ?";
        List<Facility> facilities = jdbcTemplate.query(sql, new FacilityRowMapper(), facilityId);
        return facilities.stream().findFirst();
    }

    public List<Coach> findAllCoaches() {
        String sql = "SELECT * FROM coaches ORDER BY facility_id, name";
        return jdbcTemplate.query(sql, new CoachRowMapper());
    }

    public Optional<Coach> findCoachById(Long coachId) {
        String sql = "SELECT * FROM coaches WHERE id = ?";
        List<Coach> coaches = jdbcTemplate.query(sql, new CoachRowMapper(), coachId);
        return coaches.stream().findFirst();
    }

    public int updateCoachActiveStatus(Long coachId, Boolean isActive) {
        String sql = "UPDATE coaches SET is_active = ? WHERE id = ?";
        return jdbcTemplate.update(sql, isActive, coachId);
    }

    public int updateFacilityStatus(Long facilityId, String status) {
        String sql = "UPDATE facilities SET status = ? WHERE id = ?";
        return jdbcTemplate.update(sql, status, facilityId);
    }
}
