package com.example.backend.controllers;

import com.example.backend.dtos.TeammateAdDTOs.*;
import com.example.backend.services.TeammateAdService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ads")
@CrossOrigin(origins = "http://localhost:4200")
public class TeammateAdController {

    private final TeammateAdService adService;

    public TeammateAdController(TeammateAdService adService) {
        this.adService = adService;
    }

    
    //1. KREIRANJE NOVOG OGLASA
    // POST http://localhost:8080/api/ads
    @PostMapping
    public ResponseEntity<String> createAd(@RequestBody AdRequest request, Principal principal) {
        adService.createAd(request, principal.getName());
        return ResponseEntity.ok("Oglas je uspešno postavljen!");
    }

    
    // 2. OGLASNA TABLA - PREGLED SVIH TUĐIH AKTIVNIH OGLASA
    // GET http://localhost:8080/api/ads/active
   @GetMapping("/active")
public ResponseEntity<List<AdResponse>> getActiveAds(Principal principal) {
    System.out.println("[KONTROLER] Pogodjena ruta /api/ads/active");
    
    if (principal == null) {
        System.out.println("[KONTROLER WARNING] Principal objekat je NULL! Korisnik nije autentifikovan.");
        throw new RuntimeException("Niste autorizovani.");
    } else {
        System.out.println("[KONTROLER] Principal.getName() je vratio: '" + principal.getName() + "'");
    }
    
    // Izvlačimo listu iz servisa
    List<AdResponse> ads = adService.getActiveAds(principal.getName());
    
    // Pakujemo je u ResponseEntity sa statusom 200 OK
    return ResponseEntity.ok(ads);
}

    
    // 3. SLANJE ZAHTEVA / PRIJAVA NA OGLAS SAIGRAČA
    //POST http://localhost:8080/api/ads/{adId}/respond
    @PostMapping("/{adId}/respond")
    public ResponseEntity<String> respondToAd(@PathVariable Long adId, Principal principal) {
        adService.respondToAd(adId, principal.getName());
        return ResponseEntity.ok("Uspešno ste poslali zahtev za pridruživanje!");
    }

    
    // 4. PROFIL AUTORA - PREGLED SOPSTVENIH OGLASA SA PRIJAVAMA
    // GET http://localhost:8080/api/ads/my
    @GetMapping("/my")
    public ResponseEntity<List<AdResponseDetails>> getMyAds(Principal principal) {
        List<AdResponseDetails> myAds = adService.getMyAdsWithResponses(principal.getName());
        return ResponseEntity.ok(myAds);
    }

    
    // 5. ODOBRAVANJE ILI ODBIJANJE PRIJAVE (Status može biti APPROVED ili REJECTED)
    // PUT http://localhost:8080/api/ads/responses/{responseId}/status?status=APPROVED
    @PutMapping("/responses/{responseId}/status")
    public ResponseEntity<String> handleResponse(
            @PathVariable Long responseId,
            @RequestParam("status") String status,
            Principal principal) {
        adService.handleResponseStatus(responseId, status, principal.getName());
        return ResponseEntity.ok("Status prijave uspešno ažuriran!");
    }

    
    //6. RUČNO ZATVARANJE OGLASA OD STRANE VLASNIKA
    //PUT http://localhost:8080/api/ads/{adId}/close
    @PutMapping("/{adId}/close")
    public ResponseEntity<String> closeAd(@PathVariable Long adId, Principal principal) {
        adService.closeAdManually(adId, principal.getName());
        return ResponseEntity.ok("Oglas je uspešno zatvoren.");
    }


    // 7. PREGLED OGLASA NA KOJE SE KORISNIK VEC PRIJAVIO
    // Vraca listu {ad_id, status} para da frontend zna da sakrije "Prijavi se" dugme.
    // GET http://localhost:8080/api/ads/my-responses
    @GetMapping("/my-responses")
    public ResponseEntity<List<Map<String, Object>>> getMyResponses(Principal principal) {
        return ResponseEntity.ok(adService.getMyResponses(principal.getName()));
    }
}