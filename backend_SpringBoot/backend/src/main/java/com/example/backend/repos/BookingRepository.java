package com.example.backend.repos;

import com.example.backend.dtos.ReservationViewResponse;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class BookingRepository {

    private final JdbcTemplate jdbcTemplate;

    public BookingRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    
    // 1. TABELA ZA CHECK-IN (Samo CONFIRMED rezervacije i SCHEDULED treninzi za DANAS)
    public List<ReservationViewResponse> getDashboardForEmployee(Long employeeId) {
    String sql =
            // 1. Obične rezervacije - samo za danas
            "SELECT r.id, 'RESERVATION' AS type, CONCAT(u.first_name, ' ', u.last_name) AS user_name, " +
            "r.court_id, co.court_name, NULL AS coach_name, r.reservation_date AS b_date, r.start_time, r.end_time, r.status " +
            "FROM reservations r " +
            "JOIN courts co ON r.court_id = co.id " +
            "JOIN facilities f ON co.facility_id = f.id " +
            "JOIN facility_employees fe ON f.id = fe.facility_id " +
            "JOIN users u ON r.user_id = u.id " +
            "WHERE fe.user_id = ? AND r.status = 'CONFIRMED' " +
            "AND f.status = 'APPROVED' " +
            "AND r.reservation_date = CURDATE() " +

            "UNION ALL " +

            // 2. Individualni treninzi - samo za danas
            "SELECT t.id, 'TRAINING' AS type, CONCAT(u.first_name, ' ', u.last_name) AS user_name, " +
            "t.court_id, co.court_name, ch.name AS coach_name, t.training_date AS b_date, t.start_time, t.end_time, t.status " +
            "FROM trainings t " +
            "JOIN courts co ON t.court_id = co.id " +
            "JOIN facilities f ON co.facility_id = f.id " +
            "JOIN facility_employees fe ON f.id = fe.facility_id " +
            "JOIN users u ON t.user_id = u.id " +
            "JOIN coaches ch ON t.coach_id = ch.id " +
            "WHERE fe.user_id = ? AND t.status = 'SCHEDULED' " +
            "AND f.status = 'APPROVED' " +
            "AND t.training_date = CURDATE() " +

            "ORDER BY start_time ASC";
    return jdbcTemplate.query(sql, (rs, rowNum) -> {
        ReservationViewResponse res = new ReservationViewResponse();
        res.setId(rs.getLong("id"));
        res.setType(rs.getString("type"));
        res.setUserName(rs.getString("user_name"));
        res.setCourtId(rs.getLong("court_id"));
        res.setCourtName(rs.getString("court_name"));
        res.setCoachName(rs.getString("coach_name"));
        res.setDate(rs.getString("b_date"));
        res.setStartTime(rs.getString("start_time"));
        res.setEndTime(rs.getString("end_time"));
        res.setStatus(rs.getString("status"));

        java.time.LocalDate today = java.time.LocalDate.now();
        java.time.LocalTime now = java.time.LocalTime.now();
        java.time.LocalDate bookingDate = java.time.LocalDate.parse(rs.getString("b_date"));
        java.time.LocalTime startTime = java.time.LocalTime.parse(rs.getString("start_time"));
        java.time.LocalTime deadline = startTime.plusMinutes(10);

        boolean isToday = today.equals(bookingDate);
        boolean withinTenMinutes = !now.isBefore(startTime) && !now.isAfter(deadline);
        boolean isStatusActive = "CONFIRMED".equals(res.getStatus()) || "SCHEDULED".equals(res.getStatus());

        res.setActionAllowed(isStatusActive && isToday && withinTenMinutes);

        return res;
    }, employeeId, employeeId);
}
    
    // SVE ZA KALENDAR (Izbacujemo samo CANCELLED)
   public List<ReservationViewResponse> getCalendarData(Long courtId, String startDate, String endDate) {
    String sql = 
        "SELECT r.id, 'RESERVATION' AS type, CONCAT(u.first_name, ' ', u.last_name) AS user_name, " +
        "r.court_id, co.court_name, NULL AS coach_name, r.reservation_date AS b_date, r.start_time, r.end_time, r.status " +
        "FROM reservations r " +
        "JOIN courts co ON r.court_id = co.id " +
        "JOIN facilities f ON co.facility_id = f.id " +
        "JOIN users u ON r.user_id = u.id " +
        "WHERE r.court_id = ? AND r.reservation_date BETWEEN ? AND ? AND f.status = 'APPROVED' " +
        
        "UNION ALL " +
        
        "SELECT t.id, 'TRAINING' AS type, CONCAT(u.first_name, ' ', u.last_name) AS user_name, " +
        "t.court_id, co.court_name, ch.name AS coach_name, t.training_date AS b_date, t.start_time, t.end_time, t.status " +
        "FROM trainings t " +
        "JOIN courts co ON t.court_id = co.id " +
        "JOIN facilities f ON co.facility_id = f.id " +
        "JOIN users u ON t.user_id = u.id " +
        "JOIN coaches ch ON t.coach_id = ch.id " +
        "WHERE t.court_id = ? AND t.training_date BETWEEN ? AND ? AND f.status = 'APPROVED' " +
        "ORDER BY b_date ASC, start_time ASC";

    return jdbcTemplate.query(sql, (rs, rowNum) -> {
        ReservationViewResponse res = new ReservationViewResponse();
        res.setId(rs.getLong("id"));
        res.setType(rs.getString("type"));
        res.setUserName(rs.getString("user_name"));
        res.setCourtId(rs.getLong("court_id"));
        res.setCourtName(rs.getString("court_name"));
        res.setCoachName(rs.getString("coach_name"));
        res.setDate(rs.getString("b_date"));
        res.setStartTime(rs.getString("start_time"));
        res.setEndTime(rs.getString("end_time"));
        res.setStatus(rs.getString("status"));
        res.setActionAllowed(false);
        return res;
    }, courtId, startDate, endDate, courtId, startDate, endDate);
}

    
    // ZAHTEVI NA ČEKANJU (Samo PENDING rezervacije terena za objekte ovog zaposlenog)
    
   public List<ReservationViewResponse> getPendingReservationsForEmployee(Long employeeId) {
    String sql = "SELECT r.id, 'RESERVATION' AS type, CONCAT(u.first_name, ' ', u.last_name) AS user_name, " +
            "r.court_id, co.court_name, NULL AS coach_name, r.reservation_date AS b_date, r.start_time, r.end_time, r.status " +
            "FROM reservations r " +
            "JOIN courts co ON r.court_id = co.id " +
            "JOIN facilities f ON co.facility_id = f.id " +
            "JOIN facility_employees fe ON f.id = fe.facility_id " + // Povezujemo objekat sa zaposlenim
            "JOIN users u ON r.user_id = u.id " +
            "WHERE fe.user_id = ? AND r.status = 'PENDING' " +      // Tražimo zaposlenog u međutabeli
            "AND f.status = 'APPROVED' " +
            "ORDER BY b_date ASC, start_time ASC";

    return jdbcTemplate.query(sql, (rs, rowNum) -> {
        ReservationViewResponse res = new ReservationViewResponse();
        res.setId(rs.getLong("id"));
        res.setType(rs.getString("type"));
        res.setUserName(rs.getString("user_name"));
        res.setCourtId(rs.getLong("court_id"));
        res.setCourtName(rs.getString("court_name"));
        res.setCoachName(rs.getString("coach_name"));
        res.setDate(rs.getString("b_date"));
        res.setStartTime(rs.getString("start_time"));
        res.setEndTime(rs.getString("end_time"));
        res.setStatus(rs.getString("status"));
        res.setActionAllowed(false);
        return res;
    }, employeeId);
}

    public void updateStatus(String type, Long id, String newStatus) {
        String tableName = "RESERVATION".equals(type) ? "reservations" : "trainings";
        String sql = "UPDATE " + tableName + " SET status = ? WHERE id = ?";
        jdbcTemplate.update(sql, newStatus, id);
    }
}