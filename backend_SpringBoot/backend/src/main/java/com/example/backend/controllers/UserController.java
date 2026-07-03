package com.example.backend.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.backend.dtos.AdminUserResponse;
import com.example.backend.dtos.EmployeeProfileResponse;
import com.example.backend.models.User;
import com.example.backend.services.UserService;
import com.example.backend.repos.UserRepository;
import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:4200")
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;

    public UserController(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    // PREGLED SOPSTVENOG PROFILA (sportista ili bilo koji ulogovani korisnik)
    @GetMapping("/profile")
    public ResponseEntity<AdminUserResponse> getMyProfile(Principal principal) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("Korisnik nije pronađen."));
        AdminUserResponse resp = new AdminUserResponse(
                user.getId(), user.getUsername(), user.getEmail(),
                user.getFirst_name(), user.getLast_name(), user.getPhone(),
                user.getRole(), user.getStatus(), user.getProfile_picture_path(),
                user.getFavorite_sports());
        return ResponseEntity.ok(resp);
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