package com.example.backend.repos;

import com.example.backend.dtos.EmployeeProfileResponse.EmployeeFacilityDTO;
import com.example.backend.models.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import com.example.backend.rowmappers.UserRowMapper;

@Repository
public class UserRepository {

    private final JdbcTemplate jdbcTemplate;

    // Ponovo koristimo konstruktorsku injekciju - Spring sam ubacuje JdbcTemplate
    // konfigurisan preko application.properties
    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * 1. UPIS KORISNIKA U BAZU (Registracija)
     * Koristi se klasičan INSERT upit sa "placeholderima" (?) zbog sprečavanja SQL
     * Injection napada.
     */
    public int save(User user) {
        String sql = "INSERT INTO users (username, password_hash, email, first_name, last_name, phone, role, status, favorite_sports) "
                +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        return jdbcTemplate.update(sql,
                user.getUsername(),
                user.getPassword_hash(),
                user.getEmail(),
                user.getFirst_name(),
                user.getLast_name(),
                user.getPhone(),
                user.getRole(),
                user.getStatus(),
                user.getFavorite_sports());
    }

    /**
     * 2. PRONALAŽENJE KORISNIKA PO USERNAME-U (Login i JWT provera)
     * Vraćamo Optional<User> da bismo elegantno hendlovali situaciju kada korisnik
     * ne postoji.
     */
    public Optional<User> findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";

        try {
            // queryForObject povlači tačno jedan red. Ako nema rezultata, baciće izuzetak
            // koji hvatamo u catch bloku.
            User user = jdbcTemplate.queryForObject(sql, new UserRowMapper(), username);
            return Optional.ofNullable(user);
        } catch (Exception e) {
            // Umesto da aplikacija pukne sa EmptyResultDataAccessException, vraćamo prazan
            // Optional
            return Optional.empty();
        }
    }

    // azuriranje profila sportiste
    public int updateProfile(String username, String firstName, String lastName, String phone, String email) {
        String sql = "UPDATE users SET first_name = ?, last_name = ?, phone = ?, email = ? WHERE username = ?";
        return jdbcTemplate.update(sql, firstName, lastName, phone, email, username);
    }

    public int updateFavoriteSports(String username, String favoriteSports) {
        String sql = "UPDATE users SET favorite_sports = ? WHERE username = ?";
        return jdbcTemplate.update(sql, favoriteSports, username);
    }

    public int updateProfileImage(String username, String imagePath) {
        String sql = "UPDATE users SET profile_picture_path = ? WHERE username = ?";
        return jdbcTemplate.update(sql, imagePath, username);
    }

    public List<EmployeeFacilityDTO> findFacilitiesByUserId(Long userId) {
        String sql = "SELECT f.id, f.name, f.city, f.address, f.description, f.price_per_hour, f.type, f.status, " +
                "f.company_name, f.company_address, f.company_id_number, f.pib " +
                "FROM facilities f " +
                "JOIN facility_employees fe ON f.id = fe.facility_id " +
                "WHERE fe.user_id = ?";

        return jdbcTemplate.query(sql, (rs, rowNum) -> new EmployeeFacilityDTO(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getString("city"),
                rs.getString("address"),
                rs.getString("description"),
                rs.getDouble("price_per_hour"),
                rs.getString("type"),
                rs.getString("status"),
                rs.getString("company_name"),
                rs.getString("company_address"),
                rs.getString("company_id_number"),
                rs.getString("pib")), userId);
    }

    
    // Traži ID objekta na osnovu 4 striktna administrativna parametra.
    // Vraća null ako objekat ne postoji.
    public Long findFacilityIdByDetails(String name, String companyIdNumber, String pib, String companyAddress) {
        String sql = "SELECT id FROM facilities WHERE name = ? AND company_id_number = ? AND pib = ? AND company_address = ?";

        List<Long> ids = jdbcTemplate.query(
                sql,
                (rs, rowNum) -> rs.getLong("id"),
                name, companyIdNumber, pib, companyAddress);

        return ids.isEmpty() ? null : ids.get(0);
    }

    
    // Broji koliko zaposlenih je trenutno vezano za zadati objekat u spojnoj 
    public int countEmployeesForFacility(Long facilityId) {
        String sql = "SELECT COUNT(*) FROM facility_employees WHERE facility_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, facilityId);
        return count != null ? count : 0;
    }

    
    // Upisuje novu vezu između zaposlenog i objekta u spojnu tabelu.    
    public void saveFacilityEmployeeRelation(Long facilityId, Long userId) {
        String sql = "INSERT INTO facility_employees (facility_id, user_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, facilityId, userId);
    }

    // Proverava da li je zaposleni povezan sa datim objektom
public boolean isEmployeeAssignedToFacility(Long facilityId, Long userId) {
    String sql = "SELECT COUNT(*) FROM facility_employees WHERE facility_id = ? AND user_id = ?";
    Integer count = jdbcTemplate.queryForObject(sql, Integer.class, facilityId, userId);
    return count != null && count > 0;
}

}