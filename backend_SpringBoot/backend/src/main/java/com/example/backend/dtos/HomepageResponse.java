package com.example.backend.dtos;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HomepageResponse {

    private long totalFacilities;
    private List<Map<String, Object>> topFacilities; // Lista od top 3 objekta
    private List<PromotionSummaryResponse> promotions; // Top 3 aktuelne promocije
}