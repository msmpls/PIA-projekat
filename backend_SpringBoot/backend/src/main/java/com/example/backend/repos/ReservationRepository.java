package com.example.backend.repos;

import com.example.backend.dtos.ReservationResponse;
import com.example.backend.rowmappers.ReservationResponseRowMapper;
import com.example.backend.rowmappers.ReservationRowMapper;
import com.example.backend.models.Reservation;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ReservationRepository {

    private final JdbcTemplate jdbcTemplate;

    public ReservationRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public int save(Reservation res) {
        String sql = "INSERT INTO reservations (user_id, court_id, reservation_date, start_time, end_time, status) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        return jdbcTemplate.update(sql, res.getUser_id(), res.getCourt_id(),
                res.getReservation_date(), res.getStart_time(), res.getEnd_time(), res.getStatus());
    }

    public Optional<Reservation> findById(Long id) {
        String sql = "SELECT * FROM reservations WHERE id = ?";
        try {
            Reservation res = jdbcTemplate.queryForObject(sql, new ReservationRowMapper(), id);
            return Optional.ofNullable(res);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public int updateStatus(Long id, String status) {
        String sql = "UPDATE reservations SET status = ? WHERE id = ?";
        return jdbcTemplate.update(sql, status, id);
    }

    public boolean existsOverlapping(Long courtId, String date, String startTime, String endTime) {
        String sql = "SELECT COUNT(*) FROM reservations " +
                "WHERE court_id = ? AND reservation_date = ? AND status = 'CONFIRMED' " +
                "AND (start_time < ? AND end_time > ?)";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, courtId, date, endTime, startTime);
        return count != null && count > 0;
    }

    /*
     * public List<ReservationResponse> findAllByUserId(Long userId) {
     * String sql =
     * "SELECT r.id, r.user_id, r.reservation_date, r.start_time, r.end_time, r.status, "
     * +
     * "f.name AS facility_name, f.city AS city, c.name AS court_name_number, c.sport AS sport "
     * +
     * "FROM reservations r " +
     * "JOIN courts c ON r.court_id = c.id " +
     * "JOIN facilities f ON c.facility_id = f.id " +
     * "WHERE r.user_id = ? AND f.status = 'APPROVED' " +
     * "ORDER BY r.reservation_date DESC, r.start_time DESC";
     * return jdbcTemplate.query(sql, new ReservationResponseRowMapper(), userId);
     * }
     */
    public List<ReservationResponse> findAllByUserId(Long userId) {
        String sql = "SELECT r.id, r.user_id, r.reservation_date, r.start_time, r.end_time, r.status, " +
                "f.name AS facility_name, f.city AS city, c.court_name AS court_name_number, c.sport_type AS sport " +
                "FROM reservations r " +
                "JOIN courts c ON r.court_id = c.id " +
                "JOIN facilities f ON c.facility_id = f.id " +
                "WHERE r.user_id = ? AND f.status = 'APPROVED' " +
                "ORDER BY r.reservation_date DESC, r.start_time DESC";
        return jdbcTemplate.query(sql, new ReservationResponseRowMapper(), userId);
    }

    // Pomoćna metoda za interaktivni kalendar (da Angular zna šta je zauzeto za taj
    // dan)
    public List<Reservation> findAllByCourtIdAndDate(Long courtId, String date) {
        String sql = "SELECT r.* FROM reservations r " +
                "JOIN courts c ON r.court_id = c.id " +
                "JOIN facilities f ON c.facility_id = f.id " +
                "WHERE r.court_id = ? AND reservation_date = ? AND status = 'CONFIRMED' " +
                "AND f.status = 'APPROVED'";
        return jdbcTemplate.query(sql, new ReservationRowMapper(), courtId, date);
    }

    public int countConfirmedByUserAndFacility(Long userId, Long facilityId) {
        String sql = "SELECT COUNT(*) FROM reservations r " +
                "JOIN courts c ON r.court_id = c.id " +
                "WHERE r.user_id = ? AND c.facility_id = ? AND r.status IN ('CONFIRMED','COMPLETED')";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, userId, facilityId);
        return count == null ? 0 : count;
    }
}