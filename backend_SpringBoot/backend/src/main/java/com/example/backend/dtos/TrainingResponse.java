package com.example.backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrainingResponse {
    private Long id;
    private String coach_name;       // Ime i prezime trenera
    private String facility_name;    // Naziv sportskog objekta
    private Long court_id;           // NOVO: ID terena
    private String court_name;       // NOVO: Naziv terena (npr. "Teren 1")
    private String sport;            // Koji je sport u pitanju
    private String training_date;    // Datum treninga ("YYYY-MM-DD")
    private String start_time;       // Vreme početka ("HH:MM:SS")
    private String end_time;         // Vreme završetka ("HH:MM:SS")
    private String status;           // 'SCHEDULED', 'COMPLETED', 'CANCELLED'
}