package com.example.backend.controllers;

import com.example.backend.dtos.OrderRequest;
import com.example.backend.dtos.OrderResponse;
import com.example.backend.models.Equipment;
import com.example.backend.services.ShopService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/shop")
@CrossOrigin(origins = "http://localhost:4200")
public class ShopController {

    private final ShopService shopService;

    public ShopController(ShopService shopService) {
        this.shopService = shopService;
    }

    
    // 1. PREGLED KATALOGA PO SPORTU
    //GET http://localhost:8080/api/shop/catalog?sport_type=Kosarka
     
    @GetMapping("/catalog")
    public ResponseEntity<List<Equipment>> getCatalog(@RequestParam("sport_type") String sportType) {
        List<Equipment> catalog = shopService.getCatalogBySport(sportType);
        return ResponseEntity.ok(catalog);
    }

    //2. NARUČIVANJE KORPE
    //POST http://localhost:8080/api/shop/orders
     
    @PostMapping("/orders")
    public ResponseEntity<String> placeOrder(@RequestBody OrderRequest request, Principal principal) {
        shopService.placeOrder(request, principal.getName());
        return ResponseEntity.ok("Porudžbina je uspešno kreirana!");
    }

    
    // 3. ISTORIJA I AKTIVNE PORUDŽBINE NA PROFILU
    //GET http://localhost:8080/api/shop/orders/my
     
    @GetMapping("/orders/my")
    public ResponseEntity<List<OrderResponse>> getMyOrders(Principal principal) {
        List<OrderResponse> orders = shopService.getMyOrders(principal.getName());
        return ResponseEntity.ok(orders);
    }

    
    // 4. OTKAZIVANJE PORUDŽBINE
    //PUT http://localhost:8080/api/shop/orders/5/cancel
    @PutMapping("/orders/{orderId}/cancel")
    public ResponseEntity<String> cancelOrder(@PathVariable Long orderId, Principal principal) {
        shopService.cancelOrder(orderId, principal.getName());
        return ResponseEntity.ok("Porudžbina je uspešno otkazana.");
    }
}