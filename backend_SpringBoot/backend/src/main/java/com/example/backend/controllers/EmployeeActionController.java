package com.example.backend.controllers;

import com.example.backend.dtos.OrderResponse;
import com.example.backend.dtos.ReservationViewResponse;
import com.example.backend.services.EmployeeBookingService;
import com.example.backend.services.EmployeeShopService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/employee/dashboard")
@CrossOrigin(origins = "http://localhost:4200")
public class EmployeeActionController {

    private final EmployeeBookingService employeeBookingService;
    private final EmployeeShopService employeeShopService;

    public EmployeeActionController(EmployeeBookingService employeeBookingService,EmployeeShopService employeeShopService) {
        this.employeeBookingService = employeeBookingService;
        this.employeeShopService = employeeShopService;
    }

    
    // 1. RUTA ZA TABELU 1: Ček-in dolazaka (Samo CONFIRMED/SCHEDULED za danas)
    // GET http://localhost:8080/api/employee/dashboard/table
    @GetMapping("/table")
    public ResponseEntity<List<ReservationViewResponse>> getCheckInTable(Principal principal) {
        List<ReservationViewResponse> data = employeeBookingService.getCheckInTable(principal.getName());
        return ResponseEntity.ok(data);
    }

    
    // 2. RUTA ZA TABELU 2: Zahtevi na čekanju (Samo PENDING rezervacije)
    // GET http://localhost:8080/api/employee/dashboard/pending
    @GetMapping("/pending")
    public ResponseEntity<List<ReservationViewResponse>> getPendingRequests(Principal principal) {
        List<ReservationViewResponse> data = employeeBookingService.getPendingRequests(principal.getName());
        return ResponseEntity.ok(data);
    }

    
    // 3. RUTA ZA KALENDAR: Vraća zauzeća za selektovani teren (Sve osim CANCELLED)
    //GET http://localhost:8080/api/employee/dashboard/calendar?court_id=4&startDate=2026-06-22&endDate=2026-06-28
    @GetMapping("/calendar")
public ResponseEntity<List<ReservationViewResponse>> getCalendarData(
        @RequestParam("court_id") Long courtId,
        @RequestParam("startDate") String startDate,
        @RequestParam("endDate") String endDate) {
    
    List<ReservationViewResponse> data = employeeBookingService.getCalendarData(courtId, startDate, endDate);
    return ResponseEntity.ok(data);
}

    
    // 4. AKCIJA ZA ČEK-IN TABELU: Potvrdi ili odjavi dolazak (Korisnik došao / nije došao)
    // POST http://localhost:8080/api/employee/dashboard/check-in?type=RESERVATION&id=5&action=CONFIRM
    @PostMapping("/check-in")
    public ResponseEntity<String> handleCheckIn(
            @RequestParam("type") String type,
            @RequestParam("id") Long id,
            @RequestParam("action") String action) { // "CONFIRM" ili "REJECT"
        
        employeeBookingService.processCheckInAction(type, id, action);
        return ResponseEntity.ok("Uspešno procesuirana akcija: " + action);
    }

    
    // 5. AKCIJA ZA PENDING TABELU: Odobri ili odbij administrativni zahtev
    //POST http://localhost:8080/api/employee/dashboard/pending/decide?id=5&approve=true
    @PostMapping("/pending/decide")
    public ResponseEntity<String> handleApproval(
            @RequestParam("id") Long id,
            @RequestParam("approve") boolean approve) {
        
        employeeBookingService.processPendingApproval(id, approve);
        String poruka = approve ? "Rezervacija odobrena (CONFIRMED)." : "Rezervacija odbijena (CANCELLED).";
        return ResponseEntity.ok(poruka);
    }

    // 1. Pregled svih narudžbina opreme (da zaposleni vidi šta je naručeno)
    @GetMapping("/orders")
    public ResponseEntity<List<OrderResponse>> getAllOrdersForStaff() {
        // Ovde ćeš napraviti metodu u servisu koja vraća sve narudžbine
        return ResponseEntity.ok(employeeShopService.getAllOrders());
    }

    // 2. Akcija: Zaposleni označava narudžbinu kao DELIVERED
    @PutMapping("/orders/{orderId}/deliver")
    public ResponseEntity<String> markOrderAsDelivered(@PathVariable Long orderId) {
        employeeShopService.markOrderAsDelivered(orderId);
        return ResponseEntity.ok("Narudžbina " + orderId + " je uspešno isporučena (DELIVERED).");
    }

    // 3. Akcija: Zaposleni otkazuje narudžbinu (ako npr. kupac odustane)
    @PutMapping("/orders/{orderId}/cancel")
    public ResponseEntity<String> cancelOrderAsStaff(@PathVariable Long orderId) {
        employeeShopService.cancelOrderAsStaff(orderId);
        return ResponseEntity.ok("Narudžbina " + orderId + " je otkazana i zalihe su vraćene.");
    }
}