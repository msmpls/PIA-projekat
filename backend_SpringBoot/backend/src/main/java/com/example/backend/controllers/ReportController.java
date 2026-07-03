package com.example.backend.controllers;

import com.example.backend.services.ReportService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/employee/reports")
@CrossOrigin(origins = "http://localhost:4200")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    /**
     * PDF izvestaj o popunjenosti terena za izabrani mesec.
     * GET /api/employee/reports/court-usage?month=2026-07
     */
    @GetMapping("/court-usage")
    public ResponseEntity<byte[]> courtUsageReport(@RequestParam String month, Principal principal) {
        byte[] pdf = reportService.generateCourtUsageReport(month, principal.getName());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "court-usage-" + month + ".pdf");
        headers.setContentLength(pdf.length);

        return ResponseEntity.ok().headers(headers).body(pdf);
    }

    /**
     * PDF izvestaj o prometu opreme za izabrani mesec.
     * GET /api/employee/reports/equipment-sales?month=2026-07
     */
    @GetMapping("/equipment-sales")
    public ResponseEntity<byte[]> equipmentSalesReport(@RequestParam String month, Principal principal) {
        byte[] pdf = reportService.generateEquipmentSalesReport(month, principal.getName());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "equipment-sales-" + month + ".pdf");
        headers.setContentLength(pdf.length);

        return ResponseEntity.ok().headers(headers).body(pdf);
    }
}
