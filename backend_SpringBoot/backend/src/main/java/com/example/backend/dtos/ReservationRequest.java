package com.example.backend.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationRequest {

    @NotNull(message = "ID sportskog objekta je obavezan.")
    private Long facility_id;

    @NotNull(message = "ID terena je obavezan.")
    private Long court_id;

    @NotBlank(message = "Datum rezervacije je obavezan.")
    private String reservation_date; // Format: "YYYY-MM-DD"

    @NotBlank(message = "Vremenski termin je obavezan.")
    private String start_time; // npr. "18:00-19:00"

    @NotBlank(message = "Vremenski termin je obavezan.")
    private String end_time; // npr. "18:00-19:00"
}