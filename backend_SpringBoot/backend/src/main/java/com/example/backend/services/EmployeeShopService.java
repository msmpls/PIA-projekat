package com.example.backend.services;

import com.example.backend.dtos.OrderResponse;
import com.example.backend.models.Equipment;
import com.example.backend.models.OrderItem;
import com.example.backend.repos.EquipmentRepository;
import com.example.backend.repos.OrderRepository;
import com.example.backend.repos.StaffOrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import java.io.File;
import java.nio.file.Path;

import java.util.UUID;

@Service
public class EmployeeShopService {

    private final StaffOrderRepository staffOrderRepository;
    private final OrderRepository orderRepository; // Za dohvatanje itemsa
    private final EquipmentRepository equipmentRepository;

    public EmployeeShopService(StaffOrderRepository staffOrderRepository,
            OrderRepository orderRepository,
            EquipmentRepository equipmentRepository) {
        this.staffOrderRepository = staffOrderRepository;
        this.orderRepository = orderRepository;
        this.equipmentRepository = equipmentRepository;
    }

    public List<OrderResponse> getAllOrders() {
        return staffOrderRepository.findAllOrders();
    }

    @Transactional
    public void markOrderAsDelivered(Long orderId) {
        staffOrderRepository.updateStatusDirectly(orderId, "DELIVERED");
    }

    @Transactional
    public void cancelOrderAsStaff(Long orderId) {
        // Vraćanje zaliha
        List<OrderItem> items = orderRepository.findItemsByOrderId(orderId);
        for (OrderItem item : items) {
            equipmentRepository.incrementStock(item.getEquipment_id(), item.getQuantity());
        }
        staffOrderRepository.updateStatusDirectly(orderId, "CANCELLED");
    }

    /*
     * // Metoda za dodavanje nove opreme kroz dashboard
     * 
     * @Transactional
     * public void createNewEquipment(String name, String imagePath, String
     * sportType, Double price, Integer stock, String description) {
     * // 1. Kreiramo novi Equipment objekat od prosleđenih argumenata
     * Equipment newEquipment = new Equipment();
     * newEquipment.setName(name);
     * newEquipment.setImage_path(imagePath);
     * newEquipment.setSport_type(sportType);
     * newEquipment.setPrice(price);
     * newEquipment.setStock_quantity(stock);
     * newEquipment.setDescription(description);
     * 
     * // 2. Prosleđujemo objekat repozitorijumu
     * equipmentRepository.addEquipment(newEquipment);
     * }
     */
    @Transactional
    public void createNewEquipment(String name, Double price, Integer stock, String sportType, String description,
            MultipartFile file) {
        String imagePath = null;

        // Logika za obradu slike (slično kao kod registracije)
        if (file != null && !file.isEmpty()) {
            try {
                String uploadDir = "uploads/equipment/"; // Poseban folder
                File dir = new File(uploadDir);
                if (!dir.exists())
                    dir.mkdirs();

                String fileName = "eq_" + UUID.randomUUID().toString().substring(0, 8) + ".jpg";
                Path targetPath = Paths.get(uploadDir + fileName);
                Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

                imagePath = "/" + uploadDir + fileName;
            } catch (Exception e) {
                throw new RuntimeException("Greška pri čuvanju slike opreme: " + e.getMessage());
            }
        }

        // Kreiranje objekta i čuvanje u bazi
        Equipment equipment = new Equipment();
        equipment.setName(name);
        equipment.setImage_path(imagePath); // Ovde postavljamo putanju
        equipment.setSport_type(sportType);
        equipment.setPrice(price);
        equipment.setStock_quantity(stock);
        equipment.setDescription(description);

        equipmentRepository.addEquipment(equipment);
    }

    // Metoda za promenu cene
    @Transactional
    public void updateEquipmentPrice(Long id, Double newPrice) {
        equipmentRepository.updatePrice(id, newPrice);
    }

    // Metoda za promenu zaliha (ako zaposleni ručno doda robu, npr. nova isporuka)
    @Transactional
    public void updateEquipmentStock(Long id, Integer newStock) {
        equipmentRepository.updateStock(id, newStock);
    }

    // Metoda za brisanje opreme
    @Transactional
    public void deleteEquipment(Long id) {
        equipmentRepository.deleteById(id);
    }
}