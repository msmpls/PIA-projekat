package com.example.backend.repos;

import com.example.backend.dtos.RatingResponse;
import com.example.backend.models.FacilityRating;
import com.example.backend.rowmappers.FacilityRatingRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class FacilityRatingRepository {

    private final JdbcTemplate jdbcTemplate;

    public FacilityRatingRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public long countLikes(Long facilityId) {
        String sql = "SELECT COUNT(*) FROM facility_ratings WHERE facility_id = ? AND is_like = 1";
        Long count = jdbcTemplate.queryForObject(sql, Long.class, facilityId);
        return count == null ? 0 : count;
    }

    public long countDislikes(Long facilityId) {
        String sql = "SELECT COUNT(*) FROM facility_ratings WHERE facility_id = ? AND is_like = 0";
        Long count = jdbcTemplate.queryForObject(sql, Long.class, facilityId);
        return count == null ? 0 : count;
    }

    public List<RatingResponse> findLastComments(Long facilityId, int limit) {
        String sql = "SELECT r.id, u.username, r.is_like, r.comment, r.created_at " +
                     "FROM facility_ratings r " +
                     "JOIN users u ON u.id = r.user_id " +
                     "WHERE r.facility_id = ? AND r.comment IS NOT NULL AND r.comment <> '' " +
                     "ORDER BY r.created_at DESC " +
                     "LIMIT ?";
        return jdbcTemplate.query(sql, new FacilityRatingRowMapper(), facilityId, limit);
    }

    public int countRatingsByUserAndFacility(Long userId, Long facilityId) {
        String sql = "SELECT COUNT(*) FROM facility_ratings WHERE user_id = ? AND facility_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, userId, facilityId);
        return count == null ? 0 : count;
    }

    public void save(FacilityRating rating) {
        String sql = "INSERT INTO facility_ratings (facility_id, user_id, is_like, comment, created_at) " +
                     "VALUES (?, ?, ?, ?, NOW())";
        jdbcTemplate.update(sql, rating.getFacility_id(), rating.getUser_id(),
                rating.getIs_like(), rating.getComment());
    }
}