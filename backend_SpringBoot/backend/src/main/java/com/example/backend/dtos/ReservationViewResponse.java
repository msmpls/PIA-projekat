package com.example.backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationViewResponse {
    private Long id;
    private String type;             // "RESERVATION" ili "TRAINING"
    private String userName;         // Ime i prezime sportiste
    private Long courtId;            // NOVO: ID terena za lakše filtriranje na kalendaru
    private String courtName;        // Naziv terena (npr. "Sala 1")
    private String coachName;        // Ime trenera (null za običnu rezervaciju)
    private String date;             // "YYYY-MM-DD"
    private String startTime;        // "HH:MM:SS"
    private String endTime;          // "HH:MM:SS"
    private String status;           // "SCHEDULED", "CONFIRMED", "MISSED"
    private boolean actionAllowed;   // TRUE unutar 10 min od početka termina
}