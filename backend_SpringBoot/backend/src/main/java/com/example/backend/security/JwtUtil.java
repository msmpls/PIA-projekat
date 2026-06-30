package com.example.backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {

    // Kriptografski bezbedan ključ (mora imati bar 256 bita)
    private final String SECRET_KEY = "moj_super_tajni_kljuc_za_fakultetski_projekat_koji_mora_biti_dugacak";
    private final SecretKey key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

    // 1. Izvlačenje korisničkog imena iz tokena
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // 2. Izvlačenje datuma isticanja tokena
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // NOVI SYNTAX ZA 0.12.6: koristi se parser(), verifyWith() i getPayload()
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload(); 
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // 3. Generisanje tokena za korisnika
    public String generateToken(String username, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);
        return createToken(claims, username);
    }

    // NOVI SYNTAX ZA 0.12.6: claims() umesto setClaims(), i algoritam se sam prepoznaje iz ključa
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 10 sati
                .signWith(key) // Nema potrebe za SignatureAlgorithm.HS256, JJWT sam prepoznaje algoritam
                .compact();
    }

    // 4. Validacija tokena
    public Boolean validateToken(String token, String username) {
        final String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token));
    }
}