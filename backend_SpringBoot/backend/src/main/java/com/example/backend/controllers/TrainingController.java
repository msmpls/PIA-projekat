package com.example.backend.controllers;

import com.example.backend.dtos.TrainingRequest;
import com.example.backend.dtos.TrainingResponse;
import com.example.backend.models.Coach;
import com.example.backend.services.TrainingService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/trainings")
@CrossOrigin(origins = "http://localhost:4200")
public class TrainingController {

    private final TrainingService trainingService;

    public TrainingController(TrainingService trainingService) {
        this.trainingService = trainingService;
    }

    /*
     * 1. PREGLED TRENERA PO OBJEKTU I SPORTU
     * Vraća trenere sa imenom, specijalizacijom, prosečnom ocenom i cenom po satu
     * GET http://localhost:8080/api/trainings/coaches?facility_id=1&sport=Tenis
     */
    @GetMapping("/coaches")
    public ResponseEntity<List<Coach>> getCoaches(
            @RequestParam("facility_id") Long facilityId,
            @RequestParam("sport") String sport) {
        List<Coach> coaches = trainingService.getCoachesByFacilityAndSport(facilityId, sport);
        return ResponseEntity.ok(coaches);
    }

    /*
     * 2. ZAKAZIVANJE TRENINGA PREKO FORME (Sada sa unetim court_id)
     * POST http://localhost:8080/api/trainings
     */
    @PostMapping
    public ResponseEntity<String> scheduleTraining(
            @Valid @RequestBody TrainingRequest request, 
            Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).body("Niste autorizovani. Token nedostaje.");
        }
        trainingService.scheduleTraining(request, principal.getName());
        return ResponseEntity.ok("Trening je uspešno zakazan!");
    }

    /*
     * 3. PREGLED ARHIVE I ZAKAZANIH TRENINGA NA PROFILU (Sadrži i podatke o terenu)
     * GET http://localhost:8080/api/trainings/my
     */
    @GetMapping("/my")
    public ResponseEntity<List<TrainingResponse>> getMyTrainings(Principal principal) {
        List<TrainingResponse> history = trainingService.getMyTrainings(principal.getName());
        return ResponseEntity.ok(history);
    }
}