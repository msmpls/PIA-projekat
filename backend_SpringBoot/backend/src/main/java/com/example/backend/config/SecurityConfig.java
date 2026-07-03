package com.example.backend.config;

import com.example.backend.security.JwtAuthFilter;

import jakarta.servlet.Filter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    // Injektujemo filter koji presreće zahteve i proverava JWT token
    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. Isključujemo CSRF jer ne koristimo kolačiće/sesije već JWT
                .csrf(csrf -> csrf.disable())

                // 2. Govorimo Spring Security-ju da poštuje CORS konfiguraciju iz WebConfig-a
                .cors(Customizer.withDefaults())

                // 3. Konfiguracija dozvola za rute (Endpoint-e)
                .authorizeHttpRequests(auth -> auth
                        // 1. Potpuno javne rute (Dostupne svima, čak i neulogovanim korisnicima)
                        // Dozvoljavamo sve rute iz auth namespace-a (login, register)
                        .requestMatchers("/uploads/**").permitAll() // Profilne slike i SLIKE OPREME iz prodavnice
                        .requestMatchers("/api/auth/**").permitAll()

                        // 2. Javne GET rute za pretragu (Sportista mora da vidi katalog i trenera pre
                        // nego što se odluči na akciju)
                        .requestMatchers(HttpMethod.GET, "/api/trainings/coaches").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/shop/catalog").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/sports/**").permitAll()
                        // Javni prikaz zauzetih termina za teren (koristi ga i neregistrovani korisnik)
                        .requestMatchers(HttpMethod.GET, "/api/reservations/court/**").permitAll()

                        // 3. Rute koje su ekskluzivne za sportiste ('ATHLETE')
                        // Zakazivanje treninga, naručivanje iz korpe, otkazivanje porudžbine i pregled
                        // sopstvenog profila (arhiva)
                        .requestMatchers(HttpMethod.POST, "/api/trainings").hasRole("ATHLETE")
                        .requestMatchers("/api/trainings/my").hasRole("ATHLETE")
                        .requestMatchers(HttpMethod.POST, "/api/shop/orders")
                        .hasRole("ATHLETE")
                        .requestMatchers("/api/shop/orders/my").hasRole("ATHLETE")
                        .requestMatchers("/api/shop/orders/*/cancel").hasRole("ATHLETE")
                        .requestMatchers("/api/ads/**").hasRole("ATHLETE")
                        .requestMatchers("/api/public/**").permitAll()
                        // Rezervacije: kreiranje i otkazivanje isključivo za sportiste
                        .requestMatchers(HttpMethod.POST, "/api/reservations").hasRole("ATHLETE")
                        .requestMatchers(HttpMethod.PUT, "/api/reservations/*/cancel").hasRole("ATHLETE")
                        .requestMatchers("/api/reservations/my").hasRole("ATHLETE")
                        .requestMatchers("/api/users/profile/employee").hasRole("EMPLOYEE")
                        .requestMatchers(HttpMethod.POST, "/api/facilities/create").hasRole("EMPLOYEE")
                        .requestMatchers(HttpMethod.POST, "/api/facilities/{facilityId}/courts").hasRole("EMPLOYEE")
                        .requestMatchers("/api/employee/dashboard/**").hasRole("EMPLOYEE")
                        .requestMatchers("/api/employee/promotions/**").hasRole("EMPLOYEE")
                        .requestMatchers("/api/employee/reports/**").hasRole("EMPLOYEE")
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/facilities/*/ratings/eligibility").hasRole("ATHLETE")
                        .requestMatchers(HttpMethod.POST, "/api/facilities/*/ratings").hasRole("ATHLETE")
                        // 4. Primeri restrikcija za admina i zaposlene (Zadrži i prilagodi za kasnije
                        // module)
                        // Npr. odobravanje objekata, menjanje statusa porudžbina iz "ORDERED" u
                        // "DELIVERED" itd.
                        // .requestMatchers("/api/employee/**").hasAnyRole("EMPLOYEE", "ADMIN")

                        // 5. Sve ostale rute traže validan JWT token
                        // spring prolazi kroz above kao if else, poklapanje putanje
                        // i poklapanje metode, ako npr nam se putanja poklapa a metoda ne taj zahtev
                        // upada ovde i samo se trazi da je autethicated
                        .requestMatchers("/api/employee/equipment/**").hasRole("EMPLOYEE")
                        .anyRequest().authenticated())

                // 4. Gasimo kreiranje sesija na serveru (aplikacija postaje "stateless")
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 5. Ubacujemo naš prilagođeni JWT filter pre standardnog filtera za proveru
                // lozinke
                .addFilterBefore((Filter) jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // Bin za heširanje lozinki pre upisa u bazu (koristiće se u UserService)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Menadžer zadužen za samu autentifikaciju (proveru korisnika pri logovanju)
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}