package com.example.backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationResponse {
    private Long id;
    private Long user_id;
    private String facility_name;     // Kolona: "назив објекта"
    private String city;              // Kolona: "град"
    private String court_name_number; // Kolona: "терен/хала" (npr. "Teren 1" ili "Hala A")
    private String sport;             // Kolona: "спорт"
    private String reservation_date;  // Trebaće Angularu za hronologiju
    private String start_time;        // Kolona: "интервал термин (од"
    private String end_time;          // Kolona: "до)"
    private String status;            // Kolona: "статус"
}
