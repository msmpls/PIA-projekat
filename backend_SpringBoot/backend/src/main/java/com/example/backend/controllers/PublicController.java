package com.example.backend.controllers;

import com.example.backend.dtos.HomepageResponse;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/public")
@CrossOrigin(origins = "http://localhost:4200")
public class PublicController {

    private final JdbcTemplate jdbcTemplate;

    public PublicController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/homepage")
    public ResponseEntity<HomepageResponse> getHomepageData() {
        //count sve aktivne/odobrene sportske objekte
        String countSql = "SELECT COUNT(*) FROM facilities WHERE status = 'APPROVED'";
        Long totalFacilities = jdbcTemplate.queryForObject(countSql, Long.class);
        if (totalFacilities == null) totalFacilities = 0L;

        // 2. Uzimamo top 3 objekta (pošto nemamo lajkove, uzimamo prva 3 odobrena)
        String topSql = "SELECT id, name, city, address, description, price_per_hour, type FROM facilities WHERE status = 'APPROVED' LIMIT 3";
        List<Map<String, Object>> topFacilities = jdbcTemplate.queryForList(topSql);

        // 3. Pakujemo u zajednički response
        HomepageResponse response = new HomepageResponse(totalFacilities, topFacilities);
        
        return ResponseEntity.ok(response);
    }
}