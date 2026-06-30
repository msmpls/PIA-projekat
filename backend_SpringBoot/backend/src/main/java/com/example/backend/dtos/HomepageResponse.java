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

    //vracamo broj aktivnih objekata
    // i tri TOP objekta? nemamo implementiran sistem ocenjivanja i lajkova  tkd uzimamo neka 3 objekta koja su aktivna?
    private long totalFacilities;
    private List<Map<String, Object>> topFacilities; // Lista od top 3 objekta
}