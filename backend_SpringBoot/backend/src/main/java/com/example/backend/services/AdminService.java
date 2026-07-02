package com.example.backend.services;

import com.example.backend.dtos.AdminUserResponse;
import com.example.backend.models.Coach;
import com.example.backend.models.Court;
import com.example.backend.models.Facility;
import com.example.backend.models.Sport;
import com.example.backend.models.User;
import com.example.backend.repos.AdminRepository;
import com.example.backend.repos.CourtRepository;
import com.example.backend.repos.SportsRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminService {

    private final AdminRepository adminRepository;
    private final CourtRepository courtRepository;
    private final SportsRepository sportsRepository;

    public AdminService(AdminRepository adminRepository, CourtRepository courtRepository, SportsRepository sportsRepository) {
        this.adminRepository = adminRepository;
        this.courtRepository = courtRepository;
        this.sportsRepository = sportsRepository;
    }

    public List<AdminUserResponse> getAllUsers() {
        return adminRepository.findAllUsers().stream()
                .map(user -> new AdminUserResponse(
                        user.getId(),
                        user.getUsername(),
                        user.getEmail(),
                        user.getFirst_name(),
                        user.getLast_name(),
                        user.getPhone(),
                        user.getRole(),
                        user.getStatus(),
                        user.getProfile_picture_path(),
                        user.getFavorite_sports()))
                .collect(Collectors.toList());
    }

    public void updateUserStatus(Long userId, String status) {
        String normalizedStatus = status == null ? "" : status.trim().toUpperCase();
        // Accept both REJECTED and DECLINED (legacy) and normalize to REJECTED
        if ("DECLINED".equals(normalizedStatus)) {
            normalizedStatus = "REJECTED";
        }
        if (!"APPROVED".equals(normalizedStatus) && !"REJECTED".equals(normalizedStatus)) {
            throw new RuntimeException("Status može biti samo APPROVED ili REJECTED.");
        }

        User user = adminRepository.findUserById(userId)
                .orElseThrow(() -> new RuntimeException("Korisnik sa ID-jem '" + userId + "' nije pronađen."));

        if (!"PENDING".equals(user.getStatus())) {
            throw new RuntimeException("Samo korisnik sa statusom PENDING može se promeniti.");
        }

        int updated = adminRepository.updateUserStatus(userId, normalizedStatus);
        if (updated == 0) {
            throw new RuntimeException("Nije moguće ažurirati status korisnika.");
        }
    }

    public void deleteUser(Long userId) {
        int deleted = adminRepository.deleteUserById(userId);
        if (deleted == 0) {
            throw new RuntimeException("Korisnik sa ID-jem '" + userId + "' nije pronađen.");
        }
    }

    public void updateFacilityStatus(Long facilityId, String status) {
        String normalizedStatus = status == null ? "" : status.trim().toUpperCase();
        // Accept DECLINED for backwards compatibility, normalize to REJECTED
        if ("DECLINED".equals(normalizedStatus)) {
            normalizedStatus = "REJECTED";
        }
        if (!"APPROVED".equals(normalizedStatus) && !"REJECTED".equals(normalizedStatus)) {
            throw new RuntimeException("Status objekta može biti samo APPROVED ili REJECTED.");
        }

        Facility facility = adminRepository.findFacilityById(facilityId)
                .orElseThrow(() -> new RuntimeException("Objekat sa ID-jem '" + facilityId + "' nije pronađen."));

        if (!"PENDING".equals(facility.getStatus())) {
            throw new RuntimeException("Samo objekti sa statusom PENDING mogu se ažurirati.");
        }

        int updated = adminRepository.updateFacilityStatus(facilityId, normalizedStatus);
        if (updated == 0) {
            throw new RuntimeException("Nije moguće ažurirati status objekta.");
        }
    }

    public List<Facility> getPendingFacilities() {
        return adminRepository.findPendingFacilities();
    }

    public List<Court> getFacilityCourts(Long facilityId) {
        adminRepository.findFacilityById(facilityId)
                .orElseThrow(() -> new RuntimeException("Objekat sa ID-jem '" + facilityId + "' nije pronađen."));
        return courtRepository.findByFacilityIdIncludePending(facilityId);
    }

    public List<Coach> getAllCoaches() {
        return adminRepository.findAllCoaches();
    }

    public Coach getCoachById(Long coachId) {
        return adminRepository.findCoachById(coachId)
                .orElseThrow(() -> new RuntimeException("Trener sa ID-jem '" + coachId + "' nije pronađen."));
    }

    public void updateCoachActiveStatus(Long coachId, Boolean isActive) {
        if (isActive == null) {
            throw new RuntimeException("Parametar isActive je obavezan.");
        }

        adminRepository.findCoachById(coachId)
                .orElseThrow(() -> new RuntimeException("Trener sa ID-jem '" + coachId + "' nije pronađen."));

        int updated = adminRepository.updateCoachActiveStatus(coachId, isActive);
        if (updated == 0) {
            throw new RuntimeException("Nije moguće ažurirati status trenera.");
        }
    }

    public void addSport(Sport sport) {
        if (sport == null || sport.getName() == null || sport.getName().trim().isEmpty()) {
            throw new RuntimeException("Naziv sporta je obavezan.");
        }

        String name = sport.getName().trim();
        if (sportsRepository.existsByName(name)) {
            throw new RuntimeException("Sport sa nazivom '" + name + "' već postoji.");
        }

        sport.setName(name);
        int inserted = sportsRepository.save(sport);
        if (inserted == 0) {
            throw new RuntimeException("Nije moguće dodati sport.");
        }
    }
}
