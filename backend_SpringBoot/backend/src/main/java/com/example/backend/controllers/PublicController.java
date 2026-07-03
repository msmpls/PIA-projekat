package com.example.backend.controllers;

import com.example.backend.dtos.FacilityDetailsResponse;
import com.example.backend.dtos.HomepageResponse;
import com.example.backend.dtos.PromotionSummaryResponse;
import com.example.backend.dtos.RatingSummaryResponse;
import com.example.backend.models.Court;
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

    public PublicController(JdbcTemplate jdbcTemplate, FacilityRatingService facilityRatingService) {
        this.jdbcTemplate = jdbcTemplate;
        this.facilityRatingService = facilityRatingService;
    }

    // =========================================================================
    // 1. PRETRAGA OBJEKATA (sa opcionalnim filterom po sportu)
    // GET /api/public/facilities?name=&city=&type=&sport=
    // =========================================================================
    @GetMapping("/facilities")
    public ResponseEntity<List<Map<String, Object>>> searchFacilities(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "city", required = false) String city,
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "sport", required = false) String sport) {

        StringBuilder sql = new StringBuilder(
                "SELECT f.*, GROUP_CONCAT(DISTINCT c.sport_type) AS sports " +
                "FROM facilities f " +
                "LEFT JOIN courts c ON c.facility_id = f.id " +
                "WHERE f.status = 'APPROVED'"
        );
        List<Object> params = new ArrayList<>();

        if (name != null && !name.isBlank()) {
            sql.append(" AND LOWER(f.name) LIKE ?");
            params.add("%" + name.toLowerCase() + "%");
        }
        if (city != null && !city.isBlank()) {
            sql.append(" AND LOWER(f.city) = ?");
            params.add(city.toLowerCase());
        }
        if (type != null && !type.isBlank()) {
            sql.append(" AND LOWER(f.type) = ?");
            params.add(type.toLowerCase());
        }
        if (sport != null && !sport.isBlank()) {
            sql.append(" AND EXISTS (SELECT 1 FROM courts c2 WHERE c2.facility_id = f.id AND LOWER(c2.sport_type) = LOWER(?))");
            params.add(sport);
        }

        sql.append(" GROUP BY f.id ORDER BY f.name LIMIT 50");
        List<Map<String, Object>> facilities = jdbcTemplate.queryForList(sql.toString(), params.toArray());

        return ResponseEntity.ok(facilities);
    }

    // =========================================================================
    // 2. PODACI ZA POCETNU STRANU (ukupan broj + top 3 + 3 promocije)
    // GET /api/public/homepage
    // =========================================================================
    @GetMapping("/homepage")
    public ResponseEntity<HomepageResponse> getHomepageData() {
        // 1. Broj svih odobrenih objekata
        String countSql = "SELECT COUNT(*) FROM facilities WHERE status = 'APPROVED'";
        Long totalFacilities = jdbcTemplate.queryForObject(countSql, Long.class);
        if (totalFacilities == null) totalFacilities = 0L;

        // 2. Top 3 objekta po broju lajkova
        String topSql = "SELECT f.id, f.name, f.city, f.address, f.description, f.price_per_hour, f.type, " +
                 "COALESCE(SUM(CASE WHEN r.is_like = 1 THEN 1 ELSE 0 END), 0) AS likes " +
                 "FROM facilities f " +
                 "LEFT JOIN facility_ratings r ON r.facility_id = f.id " +
                 "WHERE f.status = 'APPROVED' " +
                 "GROUP BY f.id " +
                 "ORDER BY likes DESC, f.id ASC " +
                 "LIMIT 3";
        List<Map<String, Object>> topFacilities = jdbcTemplate.queryForList(topSql);

        // 3. Top 3 aktuelne promocije (gde je danas u periodu vazenja)
        // Napomena: promotions tabela nema facility_id, promocije su vezane za sport_type
        String promoSql = "SELECT p.name AS promotion_name, p.sport_type AS sport_type, " +
                "p.start_date AS start_date, p.end_date AS end_date, p.discount_percent AS discount_percent " +
                "FROM promotions p " +
                "WHERE p.is_active = 1 AND CURDATE() BETWEEN p.start_date AND p.end_date " +
                "ORDER BY p.discount_percent DESC " +
                "LIMIT 3";
        List<Map<String, Object>> promoRows = jdbcTemplate.queryForList(promoSql);

        List<PromotionSummaryResponse> promotions = new ArrayList<>();
        for (Map<String, Object> row : promoRows) {
            promotions.add(new PromotionSummaryResponse(
                    (String) row.get("promotion_name"),
                    (String) row.get("sport_type"),
                    row.get("start_date") != null ? java.sql.Date.valueOf(row.get("start_date").toString()).toLocalDate() : null,
                    row.get("end_date") != null ? java.sql.Date.valueOf(row.get("end_date").toString()).toLocalDate() : null,
                    row.get("discount_percent") != null ? ((Number) row.get("discount_percent")).intValue() : null
            ));
        }

        HomepageResponse response = new HomepageResponse(totalFacilities, topFacilities, promotions);
        return ResponseEntity.ok(response);
    }

    // =========================================================================
    // 3. DETALJI OBJEKTA (objekat + tereni + lajkovi + galerija)
    // GET /api/public/facilities/{id}
    // =========================================================================
    @GetMapping("/facilities/{facilityId}")
    public ResponseEntity<FacilityDetailsResponse> getFacilityDetails(@PathVariable Long facilityId) {
        // 1. Pronadji objekat (mora biti APPROVED)
        String facilitySql = "SELECT * FROM facilities WHERE id = ? AND status = 'APPROVED'";
        List<Facility> facilities = jdbcTemplate.query(facilitySql, new FacilityRowMapper(), facilityId);
        if (facilities.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Facility facility = facilities.get(0);

        // 2. Pronadji sve terene/hale objekta
        String courtsSql = "SELECT * FROM courts WHERE facility_id = ? ORDER BY type, court_name";
        List<Court> courts = jdbcTemplate.query(courtsSql,
                (rs, rowNum) -> {
                    Court c = new Court();
                    c.setId(rs.getLong("id"));
                    c.setFacility_id(rs.getLong("facility_id"));
                    c.setCourt_name(rs.getString("court_name"));
                    c.setSport_type(rs.getString("sport_type"));
                    c.setType(rs.getString("type"));
                    c.setCapacity(rs.getInt("capacity"));
                    c.setEquipment_description(rs.getString("equipment_description"));
                    return c;
                }, facilityId);

        // 3. Broj svadjanja / nesvadjanja
        RatingSummaryResponse ratingSummary = facilityRatingService.getPublicSummary(facilityId);
        long likes = ratingSummary.getLikes();
        long dislikes = ratingSummary.getDislikes();

        // 4. Galerija slika
        String gallerySql = "SELECT image_path FROM facility_images WHERE facility_id = ? ORDER BY id";
        List<String> galleryImages = jdbcTemplate.queryForList(gallerySql, String.class, facilityId);

        FacilityDetailsResponse response = new FacilityDetailsResponse(facility, courts, likes, dislikes, galleryImages);
        return ResponseEntity.ok(response);
    }

    // =========================================================================
    // 4. JAVNI SAZETAK OCENA ZA OBJEKAT (lajkovi, komentari)
    // GET /api/public/facilities/{facilityId}/ratings
    // =========================================================================
    @GetMapping("/facilities/{facilityId}/ratings")
    public ResponseEntity<RatingSummaryResponse> getFacilityRatings(@PathVariable Long facilityId) {
        return ResponseEntity.ok(facilityRatingService.getPublicSummary(facilityId));
    }

    // =========================================================================
    // 5. LISTA GRADOVA (za padajuci meni u pretrazi)
    // GET /api/public/cities
    // =========================================================================
    @GetMapping("/cities")
    public ResponseEntity<List<String>> getCities() {
        String sql = "SELECT DISTINCT city FROM facilities WHERE status = 'APPROVED' ORDER BY city";
        List<String> cities = jdbcTemplate.queryForList(sql, String.class);
        return ResponseEntity.ok(cities);
    }
}
