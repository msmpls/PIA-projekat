package com.example.backend.services;

import com.example.backend.dtos.RatingEligibilityResponse;
import com.example.backend.dtos.RatingRequest;
import com.example.backend.dtos.RatingResponse;
import com.example.backend.dtos.RatingSummaryResponse;
import com.example.backend.models.Facility;
import com.example.backend.models.FacilityRating;
import com.example.backend.models.User;
import com.example.backend.repos.FacilityRatingRepository;
import com.example.backend.repos.FacilityRepository;
import com.example.backend.repos.ReservationRepository;
import com.example.backend.repos.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FacilityRatingService {

    private static final int MAX_COMMENTS_SHOWN = 5;

    private final FacilityRatingRepository ratingRepository;
    private final ReservationRepository reservationRepository;
    private final FacilityRepository facilityRepository;
    private final UserRepository userRepository;

    public FacilityRatingService(FacilityRatingRepository ratingRepository,
                                  ReservationRepository reservationRepository,
                                  FacilityRepository facilityRepository,
                                  UserRepository userRepository) {
        this.ratingRepository = ratingRepository;
        this.reservationRepository = reservationRepository;
        this.facilityRepository = facilityRepository;
        this.userRepository = userRepository;
    }

    public RatingSummaryResponse getPublicSummary(Long facilityId) {
        long likes = ratingRepository.countLikes(facilityId);
        long dislikes = ratingRepository.countDislikes(facilityId);
        List<RatingResponse> lastComments = ratingRepository.findLastComments(facilityId, MAX_COMMENTS_SHOWN);
        return new RatingSummaryResponse(likes, dislikes, lastComments);
    }

    public RatingEligibilityResponse getEligibility(String username, Long facilityId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Korisnik nije pronađen."));

        int confirmedReservations = reservationRepository.countConfirmedByUserAndFacility(user.getId(), facilityId);
        int usedRatings = ratingRepository.countRatingsByUserAndFacility(user.getId(), facilityId);
        boolean canRate = usedRatings < confirmedReservations;

        return new RatingEligibilityResponse(confirmedReservations, usedRatings, canRate);
    }

    @Transactional
    public void addRating(RatingRequest dto, String username, Long facilityId) {
        if (dto.getIs_like() == null) {
            throw new RuntimeException("Morate izabrati 'sviđa mi se' ili 'ne sviđa mi se'.");
        }
        if (dto.getComment() != null && dto.getComment().length() > 500) {
            throw new RuntimeException("Komentar ne sme prelaziti 500 karaktera.");
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Korisnik nije pronađen."));

        Facility facility = facilityRepository.findById(facilityId)
                .orElseThrow(() -> new RuntimeException("Objekat nije pronađen ili nije odobren."));

        int confirmedReservations = reservationRepository.countConfirmedByUserAndFacility(user.getId(), facility.getId());
        if (confirmedReservations == 0) {
            throw new RuntimeException("Ocenu možete ostaviti samo ako ste imali bar jednu potvrđenu rezervaciju u ovom objektu.");
        }

        int usedRatings = ratingRepository.countRatingsByUserAndFacility(user.getId(), facility.getId());
        if (usedRatings >= confirmedReservations) {
            throw new RuntimeException("Iskoristili ste sve dostupne ocene za ovaj objekat (na osnovu broja potvrđenih rezervacija).");
        }

        FacilityRating rating = new FacilityRating();
        rating.setFacility_id(facility.getId());
        rating.setUser_id(user.getId());
        rating.setIs_like(dto.getIs_like());
        rating.setComment(dto.getComment());

        ratingRepository.save(rating);
    }
}