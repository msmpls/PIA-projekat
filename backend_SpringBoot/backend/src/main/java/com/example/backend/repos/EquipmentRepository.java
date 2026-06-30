package com.example.backend.repos;

import com.example.backend.rowmappers.EquipmentRowMapper;
import com.example.backend.models.Equipment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public class EquipmentRepository {

    private final JdbcTemplate jdbcTemplate;

    public EquipmentRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // Pretraga kataloga po vrsti sporta
    public List<Equipment> findBySportType(String sportType) {
        String sql = "SELECT * FROM equipment WHERE sport_type = ?";
        return jdbcTemplate.query(sql, new EquipmentRowMapper(), sportType);
    }

    public Optional<Equipment> findById(Long id) {
        String sql = "SELECT * FROM equipment WHERE id = ?";
        List<Equipment> results = jdbcTemplate.query(sql, new EquipmentRowMapper(), id);
        return results.stream().findFirst();
    }

    // Smanjivanje zaliha kada se kupi oprema
    public int updateStock(Long id, int newStock) {
        String sql = "UPDATE equipment SET stock_quantity = ? WHERE id = ?";
        return jdbcTemplate.update(sql, newStock, id);
    }

    // pronadji najvecu aktivnu promociju za sport tip
    public Integer findActiveDiscountForSport(String sportType) {
        String sql = "SELECT MAX(discount_percent) FROM promotions " +
                "WHERE is_active = TRUE AND sport_type = ? " +
                "AND CURDATE() BETWEEN start_date AND end_date";

        try {
            Integer maxDiscount = jdbcTemplate.queryForObject(sql, Integer.class, sportType);
            return (maxDiscount != null) ? maxDiscount : 0;
        } catch (Exception e) {
            return 0;
        }
    }

    // 2. Vraćanje zaliha (koristi se pri otkazivanju)
    public void incrementStock(Long id, Integer quantity) {
        String sql = "UPDATE equipment SET stock_quantity = stock_quantity + ? WHERE id = ?";
        jdbcTemplate.update(sql, quantity, id);
    }

    // Dodavanje nove opreme
    public void addEquipment(Equipment e) {
    String sql = "INSERT INTO equipment (name, image_path, sport_type, price, stock_quantity, description) VALUES (?, ?, ?, ?, ?, ?)";
    jdbcTemplate.update(sql, 
        e.getName(), 
        e.getImage_path(), 
        e.getSport_type(), 
        e.getPrice(), 
        e.getStock_quantity(), 
        e.getDescription()
    );
}

    // Ažuriranje cene
    public int updatePrice(Long id, Double newPrice) {
        String sql = "UPDATE equipment SET price = ? WHERE id = ?";
        return jdbcTemplate.update(sql, newPrice, id);
    }

    // Brisanje opreme
    public int deleteById(Long id) {
        String sql = "DELETE FROM equipment WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }
}