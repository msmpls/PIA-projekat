package com.example.backend.controllers;

import com.example.backend.dtos.EmployeeProfileResponse;
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
import java.util.List;

@RestController
@RequestMapping("/api/facilities")
@CrossOrigin(origins = "http://localhost:4200")
public class FacilityController {

    private final FacilityService facilityService;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    public FacilityController(FacilityService facilityService, UserRepository userRepository, ObjectMapper objectMapper) {
        this.facilityService = facilityService;
        this.userRepository = userRepository;
        this.objectMapper = objectMapper;
    }

    /**
     * 1. KREIRANJE OBJEKTA IZ JSON FAJLA (FileUpload)
     * POST /api/facilities/create
     * JSON fajl se parsira u FacilityRequest DTO.
     * Podaci o firmi se automatski preuzimaju iz postojećeg objekta zaposlenog.
     */
    @PostMapping("/create")
    public ResponseEntity<String> createFacility(
            @RequestParam("file") MultipartFile file,
            Principal principal) {
        try {
            if (principal == null) {
                return ResponseEntity.status(401).body("Niste autorizovani. Token nedostaje.");
            }

            if (file == null || file.isEmpty()) {
                return ResponseEntity.badRequest().body("Greška: JSON fajl nije prosleđen.");
            }

            FacilityRequest request = objectMapper.readValue(file.getInputStream(), FacilityRequest.class);

            String username = principal.getName();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Zaposleni nije pronađen."));

            // Automatski preuzmi podatke o firmi iz postojećeg objekta zaposlenog
            String companyError = inheritCompanyInfo(request, user.getId());
            if (companyError != null) {
                return ResponseEntity.badRequest().body(companyError);
            }

            facilityService.createFacilityWithCourts(request, user.getId());

            return ResponseEntity.ok("Objekat i tereni su uspešno uvezeni i poslati na odobrenje.");

        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            return ResponseEntity.badRequest().body("Greška u formatu JSON fajla. Proverite strukturu.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Greška pri validaciji: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Sistemska greška pri uvozu: " + e.getMessage());
        }
    }

    /**
     * 2. KREIRANJE OBJEKTA IZ FORME (JSON body)
     * POST /api/facilities/create-form
     * Podaci o firmi se automatski preuzimaju iz postojećeg objekta zaposlenog.
     */
    @PostMapping("/create-form")
    public ResponseEntity<String> createFacilityFromForm(
            @RequestBody FacilityRequest request,
            Principal principal) {
        try {
            if (principal == null) {
                return ResponseEntity.status(401).body("Niste autorizovani. Token nedostaje.");
            }

            String username = principal.getName();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Zaposleni nije pronađen."));

            // Automatski preuzmi podatke o firmi iz postojećeg objekta zaposlenog
            String companyError = inheritCompanyInfo(request, user.getId());
            if (companyError != null) {
                return ResponseEntity.badRequest().body(companyError);
            }

            facilityService.createFacilityWithCourts(request, user.getId());

            return ResponseEntity.ok("Objekat je uspešno kreiran i poslat na odobrenje administratora.");

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Greška pri validaciji: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Sistemska greška: " + e.getMessage());
        }
    }

    /**
     * Pomoćna metoda: preuzima podatke o firmi (company_name, company_address,
     * company_id_number, pib) iz prvog postojećeg objekta koji je zaposleni već povezan.
     * Vraća null ako je uspešno, ili poruku o grešci ako zaposleni nema nijedan objekat.
     */
    private String inheritCompanyInfo(FacilityRequest request, Long employeeId) {
        List<EmployeeProfileResponse.EmployeeFacilityDTO> existingFacilities =
                userRepository.findFacilitiesByUserId(employeeId);

        if (existingFacilities == null || existingFacilities.isEmpty()) {
            return "Greška: Nemate nijedan postojeći objekat iz kojeg se mogu preuzeti podaci o firmi. " +
                   "Podaci o firmi se automatski preuzimaju iz vašeg prvog objekta.";
        }

        EmployeeProfileResponse.EmployeeFacilityDTO companySource = existingFacilities.get(0);
        request.setCompany_name(companySource.getCompanyName());
        request.setCompany_address(companySource.getCompanyAddress());
        request.setCompany_id_number(companySource.getCompanyIdNumber());
        request.setPib(companySource.getPib());

        return null;
    }

    /**
     * 3. DODAVANJE TERENA U POSTOJEĆI OBJEKAT
     * POST /api/facilities/{facilityId}/courts
     */
    @PostMapping("/{facilityId}/courts")
    public ResponseEntity<String> addCourtToFacility(
            @PathVariable Long facilityId,
            @RequestBody CourtRequest request,
            Principal principal) {
        try {
            if (principal == null) {
                return ResponseEntity.status(401).body("Niste autorizovani. Token nedostaje.");
            }

            String username = principal.getName();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Zaposleni nije pronađen."));

            facilityService.addCourtToExistingFacility(facilityId, request, user.getId());

            return ResponseEntity.ok("Novi teren je uspešno dodat u izabrani objekat.");

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Greška pri validaciji: " + e.getMessage());
        } catch (IllegalAccessException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Sistemska greška: " + e.getMessage());
        }
    }
}
