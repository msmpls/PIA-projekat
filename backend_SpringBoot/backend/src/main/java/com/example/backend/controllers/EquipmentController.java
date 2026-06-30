package com.example.backend.controllers;

import com.example.backend.services.EmployeeShopService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;

@RestController
@RequestMapping("/api/employee/equipment")
public class EquipmentController {

    private final EmployeeShopService employeeShopService;

    // Dodaj konstruktor za Dependency Injection
    public EquipmentController(EmployeeShopService employeeShopService) {
        this.employeeShopService = employeeShopService;
    }
/* 
    @PostMapping
    public ResponseEntity<String> addEquipment(@RequestBody Equipment equipment) {
        employeeShopService.createNewEquipment(
            equipment.getName(), 
            equipment.getImage_path(),
            equipment.getSport_type(),
            equipment.getPrice(), 
            equipment.getStock_quantity(), 
            equipment.getDescription()
        );
        return ResponseEntity.ok("Oprema je uspešno dodata.");
    }*/

        @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
public ResponseEntity<String> addEquipment(
        @RequestParam("name") String name,
        @RequestParam("price") Double price,
        @RequestParam("stock") Integer stock,
        @RequestParam("sportType") String sportType,
        @RequestParam("description") String description,
        @RequestParam("file") MultipartFile file) { // Fajl se šalje ovde

    employeeShopService.createNewEquipment(name, price, stock, sportType, description, file);
    return ResponseEntity.ok("Oprema i slika su uspešno sačuvani.");
}

    @PutMapping("/{id}/price")
    public ResponseEntity<String> updatePrice(@PathVariable Long id, @RequestBody Double newPrice) {
        // Koristi employeeShopService umesto nepostojećeg equipmentService
        employeeShopService.updateEquipmentPrice(id, newPrice);
        return ResponseEntity.ok("Cena je ažurirana.");
    }

    @PutMapping("/{id}/stock")
    public ResponseEntity<String> updateStock(@PathVariable Long id, @RequestBody Integer newStock) {
        // Koristi employeeShopService
        employeeShopService.updateEquipmentStock(id, newStock);
        return ResponseEntity.ok("Zalihe su ažurirane.");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteEquipment(@PathVariable Long id) {
        employeeShopService.deleteEquipment(id);
        return ResponseEntity.ok("Oprema je uspešno obrisana.");
    }
}