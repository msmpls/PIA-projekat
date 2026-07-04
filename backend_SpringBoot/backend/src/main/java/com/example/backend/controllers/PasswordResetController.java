package com.example.backend.controllers;

import com.example.backend.services.PasswordResetService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:4200")
public class PasswordResetController {

    private final PasswordResetService passwordResetService;

    public PasswordResetController(PasswordResetService passwordResetService) {
        this.passwordResetService = passwordResetService;
    }

    /**
     * POST /api/auth/forgot-password
     * Body: { "identifier": "username ili email" }
     * Vraca reset token (u pravoj aplikaciji bi se slao email-om, ovde vracamo direktno).
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> body) {
        String identifier = body.get("identifier");
        if (identifier == null || identifier.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Unesite korisničko ime ili email."));
        }

        String token = passwordResetService.requestPasswordReset(identifier);
        if (token == null) {
            // Iz bezbednosti ne otkrivamo da li korisnik postoji
            return ResponseEntity.ok(Map.of("message", "Ako nalog postoji, reset link je generisan."));
        }

        // U pravoj aplikaciji ovde bi se slao email sa linkom.
        // Za projekat, vracamo token direktno da frontend moze da prikaze link.
        return ResponseEntity.ok(Map.of(
            "message", "Reset link je generisan.",
            "token", token
        ));
    }

    /**
     * GET /api/auth/verify-reset-token?token=xxx
     * Proverava da li je token validan.
     */
    @GetMapping("/verify-reset-token")
    public ResponseEntity<?> verifyToken(@RequestParam String token) {
        boolean valid = passwordResetService.isTokenValid(token);
        if (valid) {
            return ResponseEntity.ok(Map.of("valid", true));
        }
        return ResponseEntity.badRequest().body(Map.of("valid", false, "error", "Token je nevažeći ili je istekao."));
    }

    /**
     * POST /api/auth/reset-password
     * Body: { "token": "xxx", "newPassword": "novaLozinka1!" }
     */
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> body) {
        String token = body.get("token");
        String newPassword = body.get("newPassword");

        if (token == null || newPassword == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Token i nova lozinka su obavezni."));
        }

        // Validacija lozinke (isto pravilo kao kod registracije)
        if (!newPassword.matches("^(?=.*[A-Z])(?=.*\\d)(?=.*[^a-zA-Z0-9])[a-zA-Z].{7,11}$")) {
            return ResponseEntity.badRequest().body(Map.of("error",
                "Lozinka mora imati 8-12 karaktera, počinjati slovom, sadržati bar jedno veliko slovo, broj i specijalni karakter."));
        }

        boolean success = passwordResetService.resetPassword(token, newPassword);
        if (success) {
            return ResponseEntity.ok(Map.of("message", "Lozinka je uspešno promenjena."));
        }
        return ResponseEntity.badRequest().body(Map.of("error", "Token je nevažeći ili je istekao."));
    }
}
