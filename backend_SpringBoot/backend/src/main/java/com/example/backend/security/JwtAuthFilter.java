package com.example.backend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    
    private final JwtUtil jwtUtil;

    //Spring sam ubacuje JwtUtil bean u filter prilikom pokretanja aplikacije
    public JwtAuthFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                    HttpServletResponse response, 
                                    FilterChain filterChain) throws ServletException, IOException {
                                        System.out.println("[FILTER] Header je: '" + request.getHeader("Authorization") + "'");
        
        // 1. Izvlačimo "Authorization" header iz HTTP zahteva
        final String authHeader = request.getHeader("Authorization");

        String username = null;
        String jwt = null;

        // 2. Proveravamo da li zaglavlje postoji i da li počinje sa "Bearer "
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7); // Čistimo reč "Bearer " da ostane samo token
           
            try {
                username = jwtUtil.extractUsername(jwt);
                 System.out.println("[FILTER] Izvučen username iz JWT tokena: '" + username + "'");
            } catch (Exception e) {
                logger.error("Nemoguće izvući korisničko ime iz tokena: " + e.getMessage());
            }
        }

        // 3. Ako imamo korisničko ime, a korisnik još uvek nije autentifikovan u ovoj niti (requestu)
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            
            try {
                // Proveravamo validnost tokena
                if (jwtUtil.validateToken(jwt, username)) {
                    
                    // Izvlačimo ulogu koju smo spakovali u token (npr. "ROLE_ATHLETE", "ROLE_ADMIN")
                    String role = jwtUtil.extractClaim(jwt, claims -> claims.get("role", String.class));
                    
                    // Spring Security zahteva da uloge počinju sa prefiksom "ROLE_" da bi hasRole() radilo ispravno
                    String springRole = role.startsWith("ROLE_") ? role : "ROLE_" + role;
                    List<SimpleGrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(springRole));

                    // Kreiramo objekat autentifikacije za Spring
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            username, null, authorities
                    );
                    
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    
                    // Ubacujemo korisnika u bezbednosni kontekst - od ovog momenta on je "prijavljen" za ovaj zahtev
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            } catch (Exception e) {
                // Ako token nije validan (istekao, neispravan, itd.), samo logujemo i nastavljamo
                // Za permitAll rute ce proci, za ostale ce Spring Security vratiti 401/403
                logger.error("Greška pri validaciji JWT tokena: " + e.getMessage());
            }
        }

        // 4. Prosleđujemo zahtev dalje na sledeći filter (ili kontroler)
        filterChain.doFilter(request, response);
    }
}