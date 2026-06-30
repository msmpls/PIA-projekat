package com.example.backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

public class TeammateAdDTOs {

    // 1. Kada korisnik kreira oglas
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AdRequest {
        private String sport;
        private String city;
        private String event_date;
        private String event_time;
        private Integer players_needed;
    }

    // 2. Za javnu tabelu svih aktivnih oglasa
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AdResponse {
        private Long id;
        private String creator_username;
        private String sport;
        private String city;
        private String event_date;
        private String event_time;
        private Integer players_needed;
        private String status;
    }

    // 3. Za profil autora - oglas + lista prijava
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AdResponseDetails {
        private Long id;
        private String sport;
        private String city;
        private String event_date;
        private String event_time;
        private Integer players_needed;
        private String status;
        private List<TeammateResponseDTO> responses;
    }

    // 4. Pojedinačna prijava unutar oglas detalja
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TeammateResponseDTO {
        private Long response_id;
        private Long user_id;
        private String username;
        private String first_name;
        private String last_name;
        private String phone;
        private String status; // PENDING, APPROVED, REJECTED
    }
}