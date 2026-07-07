package com.example.backend.repos;

import com.example.backend.rowmappers.CoachRowMapper;
import com.example.backend.models.Coach;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class CoachRepository {

    private final JdbcTemplate jdbcTemplate;

    public CoachRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    // Za individualni trening mu treba lista svih aktivnih trenera u izabranom objektu i za trazeni sport
    public List<Coach> findCoachesByFacilityAndSport(Long facilityId, String sport) {
        String sql = "SELECT c.* FROM coaches c " +
                     "JOIN facilities f ON c.facility_id = f.id " +
                     "WHERE c.facility_id = ? AND c.sport = ? AND c.is_active = TRUE " +
                     "AND f.status = 'APPROVED'";
        return jdbcTemplate.query(sql, new CoachRowMapper(), facilityId, sport);
    }
      // NOVO: Svi aktivni trenere u objektu (svi sportovi)
    public List<Coach> findAllActiveByFacility(Long facilityId) {
        String sql = "SELECT c.* FROM coaches c " +
                     "JOIN facilities f ON c.facility_id = f.id " +
                     "WHERE c.facility_id = ? AND c.is_active = TRUE " +
                     "AND f.status = 'APPROVED' " +
                     "ORDER BY c.sport, c.name";
        return jdbcTemplate.query(sql, new CoachRowMapper(), facilityId);
    }

    public boolean isCoachInApprovedFacility(Long coachId) {
        String sql = "SELECT COUNT(*) FROM coaches c " +
                     "JOIN facilities f ON c.facility_id = f.id " +
                     "WHERE c.id = ? AND f.status = 'APPROVED'";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, coachId);
        return count != null && count > 0;
    }
}