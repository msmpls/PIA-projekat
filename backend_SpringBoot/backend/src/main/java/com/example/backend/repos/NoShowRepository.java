package com.example.backend.repos;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class NoShowRepository {

    private final JdbcTemplate jdbcTemplate;

    public NoShowRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /*
     * Vraca broj nedolazaka korisnika u objektu, ili 0 ako nema zapisa.
     */
    public int getNoShowCount(Long userId, Long facilityId) {
        String sql = "SELECT count FROM no_shows WHERE user_id = ? AND facility_id = ?";
        try {
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class, userId, facilityId);
            return count != null ? count : 0;
        } catch (Exception e) {
            return 0;
        }
    }

    /*
     * Proverava da li je korisnik blokiran u objektu.
     */
    public boolean isUserBlocked(Long userId, Long facilityId) {
        String sql = "SELECT is_blocked FROM no_shows WHERE user_id = ? AND facility_id = ?";
        try {
            Boolean blocked = jdbcTemplate.queryForObject(sql, Boolean.class, userId, facilityId);
            return blocked != null && blocked;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Inkrementira broj nedolazaka. Akor count >= maxNoShows, postavlja is_blocked = true.
     */
    public void incrementNoShow(Long userId, Long facilityId, int maxNoShows) {
        // Insert or update count
        String upsertSql = "INSERT INTO no_shows (user_id, facility_id, count, is_blocked) " +
                           "VALUES (?, ?, 1, 0) " +
                           "ON DUPLICATE KEY UPDATE count = count + 1";
        jdbcTemplate.update(upsertSql, userId, facilityId);

        // Proveri da li treba blokirati
        int currentCount = getNoShowCount(userId, facilityId);
        if (currentCount >= maxNoShows) {
            jdbcTemplate.update("UPDATE no_shows SET is_blocked = 1 WHERE user_id = ? AND facility_id = ?",
                    userId, facilityId);
        }
    }

    /**
     * Vraca max_no_shows za objekat.
     */
    public int getMaxNoShows(Long facilityId) {
        String sql = "SELECT max_no_shows FROM facilities WHERE id = ?";
        try {
            Integer max = jdbcTemplate.queryForObject(sql, Integer.class, facilityId);
            return max != null ? max : 3;
        } catch (Exception e) {
            return 3;
        }
    }
}
