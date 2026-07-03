package com.example.backend.dtos;

import com.example.backend.models.Court;
import com.example.backend.models.Facility;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Odgovor za GET /api/public/facilities/{id}
 * Sadrzi sve podatke potrebne za stranicu sa detaljima objekta:
 * - sam objekat
 * - lista terena/hala
 * - broj svadjanja/nesvadjanja
 * - galerija slika
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FacilityDetailsResponse {
    private Facility facility;
    private List<Court> courts;
    private long likes;
    private long dislikes;
    private List<String> galleryImages;
}
