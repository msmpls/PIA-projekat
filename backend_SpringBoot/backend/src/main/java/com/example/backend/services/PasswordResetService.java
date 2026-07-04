package com.example.backend.services;

import com.example.backend.repos.PasswordResetRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class PasswordResetService {

    private final PasswordResetRepository resetRepository;
    private final PasswordEncoder passwordEncoder;

    public PasswordResetService(PasswordResetRepository resetRepository, PasswordEncoder passwordEncoder) {
        this.resetRepository = resetRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Generise reset link za korisnika na osnovu username-a ili email-a.
     * Vraca token (koji frontend koristi u linku) ili null ako korisnik ne postoji.
     */
    public String requestPasswordReset(String identifier) {
        Map<String, Object> user = resetRepository.findByUsernameOrEmail(identifier);
        if (user == null) {
            return null;
        }

        Long userId = ((Number) user.get("id")).longValue();
        String token = UUID.randomUUID().toString().replace("-", "");

        resetRepository.saveToken(userId, token);
        return token;
    }

    /**
     * Proverava da li je token validan (neiskoriscen, neistekao).
     */
    public boolean isTokenValid(String token) {
        return resetRepository.findValidToken(token) != null;
    }

    /**
     * Resetuje lozinku koristeci validan token.
     */
    public boolean resetPassword(String token, String newPassword) {
        Map<String, Object> tokenData = resetRepository.findValidToken(token);
        if (tokenData == null) {
            return false;
        }

        Long userId = ((Number) tokenData.get("user_id")).longValue();
        String hashedPassword = passwordEncoder.encode(newPassword);

        resetRepository.updatePassword(userId, hashedPassword);
        resetRepository.markTokenAsUsed(token);
        return true;
    }
}
