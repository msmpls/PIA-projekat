package com.example.backend.controllers;

import com.example.backend.dtos.StatisticsResponse;
import com.example.backend.services.StatisticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/statistics")
@CrossOrigin(origins = "http://localhost:4200")
public class StatisticsController {

    private final StatisticsService statisticsService;

    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    // Statistika za ulogovanog sportistu (bar chart, line chart, ukupna potrosnja)
    @GetMapping("/my")
    public ResponseEntity<StatisticsResponse> getMyStatistics(Principal principal) {
        return ResponseEntity.ok(statisticsService.getMyStatistics(principal.getName()));
    }
}
