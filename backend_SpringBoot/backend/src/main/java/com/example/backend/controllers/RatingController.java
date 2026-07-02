package com.example.backend.controllers;

import com.example.backend.dtos.RatingEligibilityResponse;
import com.example.backend.dtos.RatingRequest;
import com.example.backend.services.FacilityRatingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/facilities")
@CrossOrigin(origins = "http://localhost:4200")
public class RatingController {

    private final FacilityRatingService facilityRatingService;

    public RatingController(FacilityRatingService facilityRatingService) {
        this.facilityRatingService = facilityRatingService;
    }

    @GetMapping("/{facilityId}/ratings/eligibility")
    public ResponseEntity<RatingEligibilityResponse> getEligibility(
            @PathVariable Long facilityId, Principal principal) {
        return ResponseEntity.ok(facilityRatingService.getEligibility(principal.getName(), facilityId));
    }

    @PostMapping("/{facilityId}/ratings")
    public ResponseEntity<String> addRating(
            @PathVariable Long facilityId,
            @RequestBody RatingRequest request,
            Principal principal) {
        try {
            facilityRatingService.addRating(request, principal.getName(), facilityId);
            return ResponseEntity.ok("Ocena je uspešno sačuvana.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}