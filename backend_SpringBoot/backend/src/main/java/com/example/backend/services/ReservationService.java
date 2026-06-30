package com.example.backend.services;

import com.example.backend.dtos.ReservationRequest;
import com.example.backend.dtos.ReservationResponse;
import com.example.backend.models.Reservation;
import com.example.backend.models.User;
import com.example.backend.repos.CourtRepository;
import com.example.backend.repos.ReservationRepository;
import com.example.backend.repos.UserRepository;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final CourtRepository courtRepository;
    private final UserRepository userRepository;

    public ReservationService(ReservationRepository reservationRepository, CourtRepository courtRepository, UserRepository userRepository) {
        this.reservationRepository = reservationRepository;
        this.courtRepository = courtRepository;
        this.userRepository = userRepository;
    }

    public void createReservation(ReservationRequest dto, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Korisnik nije pronađen."));

        if (!courtRepository.isCourtInApprovedFacility(dto.getCourt_id())) {
            throw new RuntimeException("Rezervacije mogu se praviti samo za odobrene objekte.");
        }

        boolean isOverlapping = reservationRepository.existsOverlapping(
                dto.getCourt_id(), dto.getReservation_date(), dto.getStart_time(), dto.getEnd_time());
        if (isOverlapping) {
            throw new RuntimeException("Izabrani termin je već zauzet.");
        }

        Reservation res = new Reservation();
        res.setUser_id(user.getId());
        res.setCourt_id(dto.getCourt_id());
        res.setReservation_date(dto.getReservation_date());
        res.setStart_time(dto.getStart_time());
        res.setEnd_time(dto.getEnd_time());
        res.setStatus("PENDING"); // Po tekstu zadatka, zaposleni je naknadno potvrđuje

        reservationRepository.save(res);
    }

    public List<ReservationResponse> getMyReservations(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Korisnik nije pronađen."));
        return reservationRepository.findAllByUserId(user.getId());
    }

    public void cancelReservation(Long reservationId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Korisnik nije pronađen."));

        Reservation res = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Rezervacija ne postoji."));

        // Bezbednosna provera: Da li ulogovani sportista uopšte poseduje ovu
        // rezervaciju?
        if (!res.getUser_id().equals(user.getId())) {
            throw new RuntimeException("Nemate pravo da otkažete tuđu rezervaciju.");
        }

        // Provera pravila od 12 sati
        // Spajamo datum "YYYY-MM-DD" i vreme "HH:MM:SS" u jedan LocalDateTime objekat
        LocalDateTime reservationStart = LocalDateTime.parse(res.getReservation_date() + "T" + res.getStart_time());
        LocalDateTime now = LocalDateTime.now();

        Duration duration = Duration.between(now, reservationStart);

        // Ako je vreme do termina manje od 12 sati, bacamo grešku
        if (duration.toHours() < 12) {
            throw new RuntimeException(
                    "Otkazivanje nije dozvoljeno jer je do početka termina ostalo manje od 12 sati.");
        }

        // Ako je sve u redu, menjamo status u CANCELLED
        reservationRepository.updateStatus(reservationId, "CANCELLED");
    }

    public List<Reservation> getOccupiedSlotsForCourt(Long courtId, String date) {
        // Pozivamo repozitorijum koji vraća samo CONFIRMED rezervacije za taj teren i
        // dan
        return reservationRepository.findAllByCourtIdAndDate(courtId, date);
    }
}