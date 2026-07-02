package com.example.backend.controllers;

import com.example.backend.dtos.AdminUserResponse;
import com.example.backend.models.Coach;
import com.example.backend.models.Facility;
import com.example.backend.models.Sport;
import com.example.backend.services.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:4200")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/users")
    public ResponseEntity<List<AdminUserResponse>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    @GetMapping("/coaches")
    public ResponseEntity<List<Coach>> getAllCoaches() {
        return ResponseEntity.ok(adminService.getAllCoaches());
    }

    @PostMapping("/sports")
    public ResponseEntity<String> addSport(@RequestBody Sport sport) {
        adminService.addSport(sport);
        return ResponseEntity.ok("Sport je uspešno dodat.");
    }

    @GetMapping("/coaches/{coachId}")
    public ResponseEntity<Coach> getCoachById(@PathVariable("coachId") Long coachId) {
        return ResponseEntity.ok(adminService.getCoachById(coachId));
    }

    @PutMapping("/coaches/{coachId}/active")
    public ResponseEntity<String> updateCoachActiveStatus(
            @PathVariable("coachId") Long coachId,
            @RequestParam("isActive") Boolean isActive) {
        adminService.updateCoachActiveStatus(coachId, isActive);
        return ResponseEntity.ok("Status trenera je uspešno ažuriran.");
    }

    @PutMapping("/users/{userId}/status")
    public ResponseEntity<String> updateUserStatus(
            @PathVariable("userId") Long userId,
            @RequestParam("status") String status) {
        adminService.updateUserStatus(userId, status);
        return ResponseEntity.ok("Status korisnika je uspešno ažuriran.");
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable("userId") Long userId) {
        adminService.deleteUser(userId);
        return ResponseEntity.ok("Korisnik je uspešno obrisan.");
    }

    @GetMapping("/facilities/pending")
    public ResponseEntity<List<Facility>> getPendingFacilities() {
        return ResponseEntity.ok(adminService.getPendingFacilities());
    }

    @GetMapping("/facilities/{facilityId}/courts")
    public ResponseEntity<List<com.example.backend.models.Court>> getFacilityCourts(
            @PathVariable("facilityId") Long facilityId) {
        return ResponseEntity.ok(adminService.getFacilityCourts(facilityId));
    }

    @PutMapping("/facilities/{facilityId}/status")
    public ResponseEntity<String> updateFacilityStatus(
            @PathVariable("facilityId") Long facilityId,
            @RequestParam("status") String status) {
        adminService.updateFacilityStatus(facilityId, status);
        return ResponseEntity.ok("Status objekta je uspešno ažuriran.");
    }
}
