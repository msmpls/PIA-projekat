package com.example.backend.services;

import com.example.backend.dtos.ReservationViewResponse;
import com.example.backend.models.Reservation;
import com.example.backend.models.User;
import com.example.backend.repos.BookingRepository;
import com.example.backend.repos.NoShowRepository;
import com.example.backend.repos.ReservationRepository;
import com.example.backend.repos.UserRepository;
import com.example.backend.repos.CourtRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeBookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ReservationRepository reservationRepository;
    private final NoShowRepository noShowRepository;
    private final CourtRepository courtRepository;

    public EmployeeBookingService(BookingRepository bookingRepository, UserRepository userRepository,
                                  ReservationRepository reservationRepository,
                                  NoShowRepository noShowRepository,
                                  CourtRepository courtRepository) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.reservationRepository = reservationRepository;
        this.noShowRepository = noShowRepository;
        this.courtRepository = courtRepository;
    }

    private User getEmployeeOrThrow(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Zaposleni sa korisničkim imenom " + username + " nije pronađen."));
    }

    //1
    public List<ReservationViewResponse> getCheckInTable(String username) {
        User employee = getEmployeeOrThrow(username);
        return bookingRepository.getDashboardForEmployee(employee.getId());
    }

    //2
    public List<ReservationViewResponse> getCalendarData(Long courtId, String startDate, String endDate) {
        return bookingRepository.getCalendarData(courtId, startDate, endDate);
    }

    //3
    public List<ReservationViewResponse> getPendingRequests(String username) {
        User employee = getEmployeeOrThrow(username);
        return bookingRepository.getPendingReservationsForEmployee(employee.getId());
    }

    //4 - Check-in akcija: CONFIRM (korisnik dosao) ili REJECT (nije dosao = no-show)
    public void processCheckInAction(String type, Long id, String action) {
        if ("CONFIRM".equalsIgnoreCase(action)) {
            bookingRepository.updateStatus(type, id, "COMPLETED");
        } else if ("REJECT".equalsIgnoreCase(action)) {
            // Za rezervacije: postavi ABSENT i evidentiraj nedolazak
            if ("RESERVATION".equalsIgnoreCase(type)) {
                bookingRepository.updateStatus(type, id, "ABSENT");
                Reservation res = reservationRepository.findById(id).orElse(null);
                if (res != null) {
                    Long facilityId = courtRepository.getFacilityIdByCourtId(res.getCourt_id());
                    if (facilityId != null) {
                        int maxNoShows = noShowRepository.getMaxNoShows(facilityId);
                        noShowRepository.incrementNoShow(res.getUser_id(), facilityId, maxNoShows);
                    }
                }
            } else {
                // Za treninge: samo CANCELLED
                bookingRepository.updateStatus(type, id, "CANCELLED");
            }
        } else {
            throw new IllegalArgumentException("Nepoznata akcija: " + action);
        }
    }

    //5
    public void processPendingApproval(Long id, boolean approve) {
        if (approve) {
            Reservation res = reservationRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Rezervacija nije pronađena."));

            boolean overlap = reservationRepository.existsOverlappingExcluding(
                    res.getCourt_id(),
                    res.getReservation_date(),
                    res.getStart_time(),
                    res.getEnd_time(),
                    res.getId()
            );
            if (overlap) {
                throw new RuntimeException("Nije moguće odobriti rezervaciju: termin je već zauzet drugom potvrđenom rezervacijom.");
            }
        }
        String targetStatus = approve ? "CONFIRMED" : "CANCELLED";
        bookingRepository.updateStatus("RESERVATION", id, targetStatus);
    }

    //6 - Pomeranje rezervacije (drag-and-drop u kalendaru)
    public void moveReservation(Long reservationId, String newDate, String newStartTime, String newEndTime) {
        Reservation res = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Rezervacija nije pronađena."));

        // Proveri preklapanje na novom terminu
        boolean overlap = reservationRepository.existsOverlappingExcluding(
                res.getCourt_id(), newDate, newStartTime, newEndTime, reservationId);
        if (overlap) {
            throw new RuntimeException("Novi termin se preklapa sa postojećom potvrđenom rezervacijom.");
        }

        // Ažuriraj termin
        reservationRepository.updateDateTime(reservationId, newDate, newStartTime, newEndTime);
    }
}
