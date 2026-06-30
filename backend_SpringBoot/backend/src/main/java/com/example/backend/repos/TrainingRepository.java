package com.example.backend.repos;

import com.example.backend.dtos.TrainingResponse;
import com.example.backend.rowmappers.TrainingResponseRowMapper;
import com.example.backend.models.Training;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class TrainingRepository {

    private final JdbcTemplate jdbcTemplate;

    public TrainingRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    //zakazivanje novog individualnog treninga
    public int save(Training training) {
        String sql = "INSERT INTO trainings (user_id, coach_id, court_id, training_date, start_time, end_time, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        return jdbcTemplate.update(sql, training.getUser_id(), training.getCoach_id(),training.getCourt_id(),
                training.getTraining_date(), training.getStart_time(), training.getEnd_time(), training.getStatus());
    }

    // Provera da li trener već ima zakazan trening koji se preklapa sa novim zahtevom
    public boolean existsOverlappingTraining(Long coachId, String date, String startTime, String endTime) {
        String sql = "SELECT COUNT(*) FROM trainings " +
                     "WHERE coach_id = ? AND training_date = ? AND status = 'SCHEDULED' " +
                     "AND (start_time < ? AND end_time > ?)";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, coachId, date, endTime, startTime);
        return count != null && count > 0;
    }


    public List<TrainingResponse> findAllTrainingsByUserId(Long userId) {
        String sql = "SELECT t.id, c.name AS coach_name, f.name AS facility_name, " +
                     "t.court_id, crt.court_name AS court_name, " + // NOVO
                     "c.sport AS sport, t.training_date, t.start_time, t.end_time, t.status " +
                     "FROM trainings t " +
                     "JOIN coaches c ON t.coach_id = c.id " +
                     "JOIN facilities f ON c.facility_id = f.id " +
                     "JOIN courts crt ON t.court_id = crt.id " + // NOVO: spajanje sa tabelom courts
                     "WHERE t.user_id = ? AND f.status = 'APPROVED' " +
                     "ORDER BY t.training_date DESC, t.start_time DESC";
        return jdbcTemplate.query(sql, new TrainingResponseRowMapper(), userId);
    }
}