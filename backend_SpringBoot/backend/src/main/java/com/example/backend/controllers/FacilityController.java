package com.example.backend.controllers;

import com.example.backend.dtos.FacilityRequest;

import com.example.backend.dtos.FacilityRequest.CourtRequest;
import com.example.backend.models.User;
import com.example.backend.repos.UserRepository;
import com.example.backend.services.FacilityService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;

@RestController
@RequestMapping("/api/facilities")
@CrossOrigin(origins = "http://localhost:4200")
public class FacilityController {

    private final FacilityService facilityService;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper; // Dodajemo Jackson ObjectMapper za čitanje JSON-a

    public FacilityController(FacilityService facilityService, UserRepository userRepository, ObjectMapper objectMapper) {
        this.facilityService = facilityService;
        this.userRepository = userRepository;
        this.objectMapper = objectMapper;
    }

    
    // Endpoint koji podržava i File Upload (JSON fajl) i standardni unos sa forme.
    // Ruta: POST /api/facilities/create
    @PostMapping("/create")
    public ResponseEntity<String> createFacility(
            @RequestParam("file") MultipartFile file, 
            Principal principal) {
        try {
            // 1. Bezbednosna provera tokena
            if (principal == null) {
                return ResponseEntity.status(401).body("Niste autorizovani. Token nedostaje.");
            }

            // 2. Provera da li je fajl uopšte stigao i da li je prazan
            if (file == null || file.isEmpty()) {
                return ResponseEntity.badRequest().body("Greška: JSON fajl ili podaci sa forme nisu prosleđeni.");
            }

            // 3. Jackson magija: Pretvaramo sadržaj fajla (ili tekst sa forme) direktno u FacilityRequest DTO
            FacilityRequest request = objectMapper.readValue(file.getInputStream(), FacilityRequest.class);

            // 4. Izvlačimo zaposlenog iz baze preko username-a iz tokena
            String username = principal.getName();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Zaposleni nije pronađen."));

            // 5. Prosleđujemo mapirani DTO i ID u servis na validaciju i upis
            facilityService.createFacilityWithCourts(request, user.getId());

            return ResponseEntity.ok("Objekat i tereni su uspešno uvezeni i poslati na odobrenje.");

        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            // Ako struktura JSON-a u fajlu ne valja (npr. pogrešni nazivi ključeva)
            return ResponseEntity.badRequest().body("Greška u formatu JSON fajla. Proverite strukturu.");
        } catch (IllegalArgumentException e) {
            // Ako ne prođe biznis pravilo N >= 4
            return ResponseEntity.badRequest().body("Greška pri validaciji: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Sistemska greška pri uvozu: " + e.getMessage());
        }
    }

    @PostMapping("/{facilityId}/courts")
    public ResponseEntity<String> addCourtToFacility(
            @PathVariable Long facilityId,
            @RequestBody CourtRequest request,
            Principal principal) {
        try {
            // 1. Provera autentifikacije
            if (principal == null) {
                return ResponseEntity.status(401).body("Niste autorizovani. Token nedostaje.");
            }

            // 2. Izvlačenje username-a iz tokena i nalaženje ID-ja zaposlenog
            String username = principal.getName();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Zaposleni nije pronađen."));

            // 3. Poziv očišćene metode iz servisa
            facilityService.addCourtToExistingFacility(facilityId, request, user.getId());

            return ResponseEntity.ok("Novi teren je uspešno dodat u izabrani objekat.");

        } catch (IllegalArgumentException e) {
            // Greške oko naziva terena ili opisa opreme > 300 karaktera
            return ResponseEntity.badRequest().body("Greška pri validaciji: " + e.getMessage());
        } catch (IllegalAccessException e) {
            // Greška ako zaposleni pokušava da menja tuđi objekat
            return ResponseEntity.status(403).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Sistemska greška: " + e.getMessage());
        }
    }
}