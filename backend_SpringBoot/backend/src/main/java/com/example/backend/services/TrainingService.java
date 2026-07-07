package com.example.backend.services;

import com.example.backend.dtos.TrainingRequest;
import com.example.backend.dtos.TrainingResponse;
import com.example.backend.models.Coach;
import com.example.backend.models.Training;
import com.example.backend.models.User;
import com.example.backend.repos.CoachRepository;
import com.example.backend.repos.CourtRepository;
import com.example.backend.repos.TrainingRepository;
import com.example.backend.repos.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TrainingService {

    private final TrainingRepository trainingRepository;
    private final CoachRepository coachRepository;
    private final CourtRepository courtRepository;
    private final UserRepository userRepository; // Pretpostavka da imaš UserRepository za izvlačenje korisnika

    public TrainingService(TrainingRepository trainingRepository, 
                                     CoachRepository coachRepository, 
                                     CourtRepository courtRepository,
                                     UserRepository userRepository) {
        this.trainingRepository = trainingRepository;
        this.coachRepository = coachRepository;
        this.courtRepository = courtRepository;
        this.userRepository = userRepository;
    }

    // 1. Pregled trenera po objektu i sportu (Javna funkcionalnost)
    public List<Coach> getCoachesByFacilityAndSport(Long facilityId, String sport) {
        return coachRepository.findCoachesByFacilityAndSport(facilityId, sport);
    }

    // 2. Zakazivanje individualnog treninga preko forme
    public void scheduleTraining(TrainingRequest dto, String username) {
        // Pronalazimo ulogovanog sportistu preko username-a iz Principal-a
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Korisnik nije pronađen."));

        if (!coachRepository.isCoachInApprovedFacility(dto.getCoach_id())) {
            throw new RuntimeException("Individualni trening može se zakazati samo za trenera u odobrenom objektu.");
        }

        if (!courtRepository.isCourtInApprovedFacility(dto.getCourt_id())) {
            throw new RuntimeException("Individualni trening može se zakazati samo na terenu u odobrenom objektu.");
        }

        // Provera da li je trener zauzet u tom terminu
        boolean isOverlapping = trainingRepository.existsOverlappingTraining(
                dto.getCoach_id(), dto.getTraining_date(), dto.getStart_time(), dto.getEnd_time()
        );
        
        if (isOverlapping) {
            throw new RuntimeException("Izabrani trener je već zauzet u tom terminu.");
        }

        // Mapiranje DTO -> Model
        Training training = new Training();
        training.setUser_id(user.getId());
        training.setCoach_id(dto.getCoach_id());
        training.setCourt_id(dto.getCourt_id());
        training.setTraining_date(dto.getTraining_date());
        
        // Ako sa frontenda stiže samo "10:00", dodajemo sekunde za bazu ("10:00:00")
        String formattedStart = dto.getStart_time().length() == 5 ? dto.getStart_time() + ":00" : dto.getStart_time();
        String formattedEnd = dto.getEnd_time().length() == 5 ? dto.getEnd_time() + ":00" : dto.getEnd_time();
        
        training.setStart_time(formattedStart);
        training.setEnd_time(formattedEnd);
        training.setStatus("SCHEDULED"); // Podrazumevani status pri zakazivanju

        trainingRepository.save(training);
    }

    // 3. Arhiva održanih i zakazanih treninga za profil
    public List<TrainingResponse> getMyTrainings(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Korisnik nije pronađen."));
        
        return trainingRepository.findAllTrainingsByUserId(user.getId());
    }

    // NOVO: Svi trenere u objektu (za coach preview)
    public List<Coach> getAllCoachesByFacility(Long facilityId) {
        return coachRepository.findAllActiveByFacility(facilityId);
    }
}