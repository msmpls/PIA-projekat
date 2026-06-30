package com.example.backend.controllers;

import com.example.backend.dtos.ReservationRequest;
import com.example.backend.dtos.ReservationResponse;
import com.example.backend.models.Reservation;
import com.example.backend.services.ReservationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@CrossOrigin(origins = "http://localhost:4200")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping
    public ResponseEntity<String> createReservation(@Valid @RequestBody ReservationRequest request,
            Principal principal) {
        reservationService.createReservation(request, principal.getName());
        return ResponseEntity.ok("Rezervacija je uspešno poslata na čekanje!");
    }

    @GetMapping("/my")
    public ResponseEntity<List<ReservationResponse>> getMyReservations(Principal principal) {
        List<ReservationResponse> responses = reservationService.getMyReservations(principal.getName());
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<String> cancelReservation(@PathVariable Long id, Principal principal) {
        reservationService.cancelReservation(id, principal.getName());
        return ResponseEntity.ok("Rezervacija je uspešno otkazana.");
    }

    @GetMapping("/court/{courtId}")
    public ResponseEntity<List<Reservation>> getCourtReservations(
            @PathVariable Long courtId,
            @RequestParam String date) {

        // Ova ruta je JAVNA (dostupna i neregistrovanom korisniku jer i on vidi detalje
        // i cene)
        // Zato ovde NE TRAŽIMO Principal objekat!

        List<Reservation> occupiedSlots = reservationService.getOccupiedSlotsForCourt(courtId, date);
        return ResponseEntity.ok(occupiedSlots);
    }
}