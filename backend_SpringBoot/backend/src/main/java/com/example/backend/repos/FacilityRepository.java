package com.example.backend.repos;

import com.example.backend.models.Facility;
import com.example.backend.rowmappers.FacilityRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;

@Repository
public class FacilityRepository {

    private final JdbcTemplate jdbcTemplate;

    public FacilityRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /*
     * Upisuje objekat u bazu i vraća njegov generisani ID.
     */
    public int save(Facility facility) {
        String sql = "INSERT INTO facilities (name, city, address, description, price_per_hour, type, status, " +
                     "created_by, company_name, company_address, company_id_number, pib, work_start_time, work_end_time) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        return jdbcTemplate.update(sql,
                facility.getName(),
                facility.getCity(),
                facility.getAddress(),
                facility.getDescription(),
                facility.getPrice_per_hour(),
                facility.getType(),
                facility.getStatus(),
                facility.getCreated_by(),
                facility.getCompany_name(),
                facility.getCompany_address(),
                facility.getCompany_id_number(),
                facility.getPib(),
                facility.getWork_start_time(),
                facility.getWork_end_time());
    }

    public Optional<Facility> findById(Long id) {
        String sql = "SELECT * FROM facilities WHERE id = ? AND status = 'APPROVED'";
        List<Facility> results = jdbcTemplate.query(sql, new FacilityRowMapper(), id);
        return results.stream().findFirst();
    }
}