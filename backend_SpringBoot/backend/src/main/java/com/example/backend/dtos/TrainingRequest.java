package com.example.backend.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrainingRequest {
    @NotNull(message = "ID trenera je obavezan.")
    private Long coach_id;

    @NotNull(message = "ID terena je obavezan.") // Novo polje
    private Long court_id;

    @NotBlank(message = "Datum treninga je obavezan.")
    private String training_date;

    @NotBlank(message = "Vreme početka je obavezno.")
    private String start_time;

    @NotBlank(message = "Vreme završetka je obavezno.")
    private String end_time;
}