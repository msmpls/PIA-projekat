package com.example.backend.services;

import com.example.backend.dtos.ReservationViewResponse;
import com.example.backend.models.User;
import com.example.backend.repos.BookingRepository;
import com.example.backend.repos.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeBookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;

    public EmployeeBookingService(BookingRepository bookingRepository, UserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
    }

    // Pomoćna metoda za pronalaženje zaposlenog na osnovu username-a iz Principal-a
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
    // Ovde prosleđuješ parametre ka repozitorijumu
    return bookingRepository.getCalendarData(courtId, startDate, endDate);
}

    //3
    public List<ReservationViewResponse> getPendingRequests(String username) {
        User employee = getEmployeeOrThrow(username);
        return bookingRepository.getPendingReservationsForEmployee(employee.getId());
    }
    //4
    public void processCheckInAction(String type, Long id, String action) {
        if ("CONFIRM".equalsIgnoreCase(action)) {
            bookingRepository.updateStatus(type, id, "COMPLETED");
        } else if ("REJECT".equalsIgnoreCase(action)) {
            bookingRepository.updateStatus(type, id, "CANCELLED");
    
        } else {
            throw new IllegalArgumentException("Nepoznata akcija: " + action);
        }
    }
    //5
    public void processPendingApproval(Long id, boolean approve) {
        String targetStatus = approve ? "CONFIRMED" : "CANCELLED";
        bookingRepository.updateStatus("RESERVATION", id, targetStatus);
    }
}