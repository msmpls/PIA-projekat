package com.example.backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Kratak prikaz promocije za pocetnu stranicu.
 * Sadrzi naziv promocije, sport, period vazenja i popust.
 * (promotions tabela nema facility_id, promocije su vezane za sport_type)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PromotionSummaryResponse {
    private String promotionName;
    private String sportType;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer discountPercent;
}
