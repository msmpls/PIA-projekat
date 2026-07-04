package com.example.backend.repos;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class PasswordResetRepository {

    private final JdbcTemplate jdbcTemplate;

    public PasswordResetRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Pronalazi korisnika po username-u ili email-u.
     */
    public Map<String, Object> findByUsernameOrEmail(String identifier) {
        String sql = "SELECT id, username, email FROM users WHERE username = ? OR email = ?";
        try {
            return jdbcTemplate.queryForMap(sql, identifier, identifier);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Cuva novi reset token (vazi 30 minuta).
     */
    public void saveToken(Long userId, String token) {
        String sql = "INSERT INTO password_reset_tokens (user_id, token, expires_at) " +
                     "VALUES (?, ?, DATE_ADD(NOW(), INTERVAL 30 MINUTE))";
        jdbcTemplate.update(sql, userId, token);
    }

    /**
     * Pronalazi validan (neiskoriscen, neistekao) token.
     */
    public Map<String, Object> findValidToken(String token) {
        String sql = "SELECT * FROM password_reset_tokens " +
                     "WHERE token = ? AND used = 0 AND expires_at > NOW()";
        try {
            return jdbcTemplate.queryForMap(sql, token);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Označava token kao iskoriscen.
     */
    public void markTokenAsUsed(String token) {
        jdbcTemplate.update("UPDATE password_reset_tokens SET used = 1 WHERE token = ?", token);
    }

    /**
     * Azurira lozinku korisnika.
     */
    public void updatePassword(Long userId, String hashedPassword) {
        jdbcTemplate.update("UPDATE users SET password_hash = ? WHERE id = ?", hashedPassword, userId);
    }
}
