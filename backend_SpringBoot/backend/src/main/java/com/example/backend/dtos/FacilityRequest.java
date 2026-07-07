package com.example.backend.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FacilityRequest {
    // Osnovni podaci o objektu
    private String name;
    private String city;
    private String address;
    private String description;
    private BigDecimal price_per_hour;
    private String type;
    private String status;

    // Radno vreme
    private String work_start_time;
    private String work_end_time;

    // Broj dozvoljenih nedolazaka pre blokade
    private Integer max_no_shows;

    // Podaci o firmi (se automatski preuzimaju iz postojećeg objekta zaposlenog)
    private String company_name;
    private String company_address;
    private String company_id_number;
    private String pib;

    // Lista terena koji se šalju uz objekat
    private List<CourtRequest> courts;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CourtRequest {
        private String court_name;
        private String sport_type;
        private String type; // "OPENED" ili "CLOSED"
        private int capacity;
        private String equipment_description;
    }
}
