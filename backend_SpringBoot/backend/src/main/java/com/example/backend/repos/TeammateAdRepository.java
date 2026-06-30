package com.example.backend.repos;

import com.example.backend.dtos.TeammateAdDTOs.AdResponse;
import com.example.backend.dtos.TeammateAdDTOs.TeammateResponseDTO;
import com.example.backend.rowmappers.TeammateAdMappers;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Map;

@Repository
public class TeammateAdRepository {

    private final JdbcTemplate jdbcTemplate;

    public TeammateAdRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void saveAd(Long creatorId, String sport, String city, String date, String time, int players) {
        String sql = "INSERT INTO teammate_ads (creator_id, sport, city, event_date, event_time, players_needed, status) VALUES (?, ?, ?, ?, ?, ?, 'OPEN')";
        jdbcTemplate.update(sql, creatorId, sport, city, date, time, players);
    }

    public List<AdResponse> findActiveAdsExcludingUser(Long userId) {
        String sql = "SELECT a.*, u.username AS creator_username FROM teammate_ads a JOIN users u ON a.creator_id = u.id WHERE a.status = 'OPEN' AND a.creator_id != ? ORDER BY a.event_date ASC";
        return jdbcTemplate.query(sql, new TeammateAdMappers.AdResponseRowMapper(), userId);
    }

    public List<AdResponse> findAdsByCreatorId(Long creatorId) {
        String sql = "SELECT a.*, u.username AS creator_username FROM teammate_ads a JOIN users u ON a.creator_id = u.id WHERE a.creator_id = ? ORDER BY a.event_date DESC";
        return jdbcTemplate.query(sql, new TeammateAdMappers.AdResponseRowMapper(), creatorId);
    }

    public List<TeammateResponseDTO> findResponsesByAdId(Long adId) {
        String sql = "SELECT r.id AS response_id, r.status, u.id AS user_id, u.username, u.first_name, u.last_name, u.phone FROM teammate_ad_responses r JOIN users u ON r.user_id = u.id WHERE r.ad_id = ?";
        return jdbcTemplate.query(sql, new TeammateAdMappers.TeammateResponseRowMapper(), adId);
    }

    public void saveResponse(Long adId, Long userId) {
        String sql = "INSERT INTO teammate_ad_responses (ad_id, user_id, status) VALUES (?, ?, 'PENDING')";
        jdbcTemplate.update(sql, adId, userId);
    }

    public boolean hasUserAlreadyResponded(Long adId, Long userId) {
        String sql = "SELECT COUNT(*) FROM teammate_ad_responses WHERE ad_id = ? AND user_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, adId, userId);
        return count != null && count > 0;
    }

    public void updateResponseStatus(Long responseId, String status) {
        String sql = "UPDATE teammate_ad_responses SET status = ? WHERE id = ?";
        jdbcTemplate.update(sql, status, responseId);
    }

    public void updateAdStatus(Long adId, String status) {
        String sql = "UPDATE teammate_ads SET status = ? WHERE id = ?";
        jdbcTemplate.update(sql, status, adId);
    }

    public void decrementPlayersNeeded(Long adId) {
        String sql = "UPDATE teammate_ads SET players_needed = players_needed - 1 WHERE id = ?";
        jdbcTemplate.update(sql, adId);
    }

    public Map<String, Object> findAdInfoByResponseId(Long responseId) {
        String sql = "SELECT a.id AS ad_id, a.players_needed, a.creator_id FROM teammate_ads a JOIN teammate_ad_responses r ON a.id = r.ad_id WHERE r.id = ?";
        return jdbcTemplate.queryForMap(sql, responseId);
    }
    
    public Map<String, Object> findAdById(Long adId) {
        String sql = "SELECT creator_id, status FROM teammate_ads WHERE id = ?";
        return jdbcTemplate.queryForMap(sql, adId);
    }
}