package com.example.backend.repos;

import com.example.backend.models.Court;
import com.example.backend.rowmappers.CourtRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CourtRepository {

    private final JdbcTemplate jdbcTemplate;

    // Konstruktorska injekcija - Spring sam ubacuje konfigurisan JdbcTemplate
    public CourtRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /*
     * 1. UPIS TERENA/HALE U BAZU
     * Koristi klasičan INSERT sa placeholderima (?) radi bezbednosti.
     */
    public int save(Court court) {
        String sql = "INSERT INTO courts (facility_id, court_name, sport_type, type, capacity, equipment_description) "
                +
                "VALUES (?, ?, ?, ?, ?, ?)";

        return jdbcTemplate.update(sql,
                court.getFacility_id(),
                court.getCourt_name(),
                court.getSport_type(),
                court.getType(),
                court.getCapacity(),
                court.getEquipment_description());
    }

    /*
     * 2. PRONALAŽENJE SVIH TERENA ZA ODREĐENI OBJEKAT
     * Koristi se kada se učitavaju detalji objekta da povučemo i sve njegove
     * terene.
     */
    public List<Court> findByFacilityId(Long facilityId) {
        String sql = "SELECT c.* FROM courts c " +
                     "JOIN facilities f ON c.facility_id = f.id " +
                     "WHERE c.facility_id = ? AND f.status = 'APPROVED'";
        return jdbcTemplate.query(sql, new CourtRowMapper(), facilityId);
    }

    public List<Court> findByFacilityIdIncludePending(Long facilityId) {
        String sql = "SELECT * FROM courts WHERE facility_id = ?";
        return jdbcTemplate.query(sql, new CourtRowMapper(), facilityId);
    }

    public boolean existsByFacilityIdAndCourtName(Long facilityId, String courtName) {
        String sql = "SELECT COUNT(*) FROM courts WHERE facility_id = ? AND court_name = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, facilityId, courtName);
        return count != null && count > 0;
    }

    public boolean isCourtInApprovedFacility(Long courtId) {
        String sql = "SELECT COUNT(*) FROM courts c " +
                     "JOIN facilities f ON c.facility_id = f.id " +
                     "WHERE c.id = ? AND f.status = 'APPROVED'";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, courtId);
        return count != null && count > 0;
    }

    /*
     * Vraca facility_id za dati court_id.
     */
    public Long getFacilityIdByCourtId(Long courtId) {
        String sql = "SELECT facility_id FROM courts WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, Long.class, courtId);
        } catch (Exception e) {
            return null;
        }
    }

}