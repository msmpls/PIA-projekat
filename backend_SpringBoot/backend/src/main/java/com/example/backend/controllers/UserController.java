package com.example.backend.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.backend.dtos.EmployeeProfileResponse;
import com.example.backend.services.UserService;
import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:4200")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // aziriranje ličnih podataka (ime, prezime, telefon, email)
    @PutMapping("/profile")
    public ResponseEntity<String> updateProfile(@RequestBody Map<String, String> data, Principal principal) {
        String username = principal.getName();
        userService.updateProfileDetails(username, data.get("first_name"), data.get("last_name"), data.get("phone"),
                data.get("email"));
        return ResponseEntity.ok("Profil je uspešno ažuriran!");
    }

    // azuriranje liste omiljenih sportova
    @PutMapping("/profile/sports")
    public ResponseEntity<String> updateFavoriteSports(@RequestBody String favoriteSports, Principal principal) {
        String username = principal.getName();
        // Čistimo navodnike ako Angular pošalje čist string sa viškom navodnika
        String cleanedSports = favoriteSports.replace("\"", "");
        userService.updateSports(username, cleanedSports);
        return ResponseEntity.ok("Omiljeni sportovi su ažurirani!");
    }

    @PutMapping("/profile/image")
    public ResponseEntity<Map<String, String>> updateProfileImage(@RequestParam("image") MultipartFile file,
            Principal principal) {
        String username = principal.getName();
        String savedPath = userService.updateImage(username, file);

        // Najbolje je vratiti JSON sa novom putanjom da Angular može odmah da osveži
        // sliku na ekranu
        return ResponseEntity.ok(Map.of("profile_picture_path", savedPath));
    }

    @GetMapping("/profile/employee")
    public ResponseEntity<EmployeeProfileResponse> getEmployeeProfile(Principal principal) {
        // Izvlačimo username direktno iz JWT tokena preko Principal-a, isto kao i u
        // tvojim ostalim rutama
        String username = principal.getName();

        EmployeeProfileResponse profileData = userService.getEmployeeProfileData(username);
        return ResponseEntity.ok(profileData);
    }
}