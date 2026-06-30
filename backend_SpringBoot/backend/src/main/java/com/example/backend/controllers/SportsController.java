package com.example.backend.controllers;

import com.example.backend.repos.SportsRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/sports")
@CrossOrigin(origins = "http://localhost:4200")
public class SportsController {

    private final SportsRepository sportsRepository;

    public SportsController(SportsRepository sportsRepository) {
        this.sportsRepository = sportsRepository;
    }

    @GetMapping("/names")
    public ResponseEntity<List<String>> getAllSportNames() {
        List<String> sportNames = sportsRepository.findAllSportNames();
        return ResponseEntity.ok(sportNames);
    }
}
