package com.example.backend.controllers;

import com.example.backend.dtos.HomepageResponse;
import com.example.backend.dtos.RatingSummaryResponse;
import com.example.backend.models.Facility;
import com.example.backend.rowmappers.FacilityRowMapper;
import com.example.backend.services.FacilityRatingService;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/public")
@CrossOrigin(origins = "http://localhost:4200")
public class PublicController {

    private final JdbcTemplate jdbcTemplate;
    private final FacilityRatingService facilityRatingService;

    public PublicController(JdbcTemplate jdbcTemplate,FacilityRatingService facilityRatingService) {
        this.jdbcTemplate = jdbcTemplate;
        this.facilityRatingService = facilityRatingService;
    }

    @GetMapping("/facilities")
    public ResponseEntity<List<Facility>> searchFacilities(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "city", required = false) String city,
            @RequestParam(value = "type", required = false) String type) {

        String baseSql = "SELECT * FROM facilities WHERE status = 'APPROVED'";
        List<Object> params = new ArrayList<>();

        if (name != null && !name.isBlank()) {
            baseSql += " AND LOWER(name) LIKE ?";
            params.add("%" + name.toLowerCase() + "%");
        }
        if (city != null && !city.isBlank()) {
            baseSql += " AND LOWER(city) = ?";
            params.add(city.toLowerCase());
        }
        if (type != null && !type.isBlank()) {
            baseSql += " AND LOWER(type) = ?";
            params.add(type.toLowerCase());
        }

        baseSql += " ORDER BY name LIMIT 50";
        List<Facility> facilities = params.isEmpty()
                ? jdbcTemplate.query(baseSql, new FacilityRowMapper())
                : jdbcTemplate.query(baseSql, new FacilityRowMapper(), params.toArray());

        return ResponseEntity.ok(facilities);
    }

    @GetMapping("/homepage")
    public ResponseEntity<HomepageResponse> getHomepageData() {
        //count sve aktivne/odobrene sportske objekte
        String countSql = "SELECT COUNT(*) FROM facilities WHERE status = 'APPROVED'";
        Long totalFacilities = jdbcTemplate.queryForObject(countSql, Long.class);
        if (totalFacilities == null) totalFacilities = 0L;

        // 2. Uzimamo top 3 objekta (pošto nemamo lajkove, uzimamo prva 3 odobrena)
        //String topSql = "SELECT id, name, city, address, description, price_per_hour, type FROM facilities WHERE status = 'APPROVED' LIMIT 3";
        String topSql = "SELECT f.id, f.name, f.city, f.address, f.description, f.price_per_hour, f.type, " +
                 "COALESCE(SUM(CASE WHEN r.is_like = 1 THEN 1 ELSE 0 END), 0) AS likes " +
                 "FROM facilities f " +
                 "LEFT JOIN facility_ratings r ON r.facility_id = f.id " +
                 "WHERE f.status = 'APPROVED' " +
                 "GROUP BY f.id " +
                 "ORDER BY likes DESC, f.id ASC " +
                 "LIMIT 3";
        List<Map<String, Object>> topFacilities = jdbcTemplate.queryForList(topSql);

        // 3. Pakujemo u zajednički response
        HomepageResponse response = new HomepageResponse(totalFacilities, topFacilities);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/facilities/{facilityId}/ratings")
public ResponseEntity<RatingSummaryResponse> getFacilityRatings(@PathVariable Long facilityId) {
    return ResponseEntity.ok(facilityRatingService.getPublicSummary(facilityId));
}
}