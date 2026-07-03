package com.example.backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * Odgovor za GET /api/statistics/my
 * Sadrzi podatke za 3 dijagrama:
 * 1. Bar chart - broj rezervacija po sportu
 * 2. Line chart - mesecna aktivnost (rezervacije + treninzi)
 * 3. Ukupna potrosnja na opremu
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatisticsResponse {
    private List<Map<String, Object>> reservationsBySport;  // [{sport, count}]
    private List<Map<String, Object>> monthlyActivity;       // [{month, count}]
    private double totalSpendingOnEquipment;
}
