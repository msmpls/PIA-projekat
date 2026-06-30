package com.example.backend.services;

import com.example.backend.dtos.CartItemRequest;
import com.example.backend.dtos.OrderRequest;
import com.example.backend.dtos.OrderResponse;
import com.example.backend.models.Equipment;
import com.example.backend.models.OrderItem;
import com.example.backend.models.User;
import com.example.backend.repos.EquipmentRepository;
import com.example.backend.repos.OrderRepository;
import com.example.backend.repos.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ShopService {

    private final EquipmentRepository equipmentRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    public ShopService(EquipmentRepository equipmentRepository,
            OrderRepository orderRepository,
            UserRepository userRepository) {
        this.equipmentRepository = equipmentRepository;
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
    }

    // 1. Pregled kataloga po vrsti sporta
    public List<Equipment> getCatalogBySport(String sportType) {
        return equipmentRepository.findBySportType(sportType);
    }

    // 2. Kreiranje porudžbine iz korpe (Transakciono!)

    // 3. Istorija i aktivne porudžbine na profilu
    public List<OrderResponse> getMyOrders(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Korisnik nije pronađen."));
        return orderRepository.findAllByUserId(user.getId());
    }

    // 4. Otkazivanje aktivne porudžbine
    @Transactional
    public void placeOrder(OrderRequest dto, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Korisnik nije pronađen."));

        double totalFinalPrice = 0.0;

        // 1. Provera zaliha i računanje totalne cene sa popustom
        for (CartItemRequest item : dto.getItems()) {
            Equipment eq = equipmentRepository.findById(item.getEquipment_id())
                    .orElseThrow(() -> new RuntimeException("Artikal ne postoji."));

            if (eq.getStock_quantity() < item.getQuantity()) {
                throw new RuntimeException("Nema dovoljno zaliha za: " + eq.getName());
            }

            // Dobavi najveći aktivni popust
            int discount = equipmentRepository.findActiveDiscountForSport(eq.getSport_type());
            double finalItemPrice = eq.getPrice() * (1 - discount / 100.0);

            totalFinalPrice += finalItemPrice * item.getQuantity();
        }

        // 2. Kreiraj glavnu porudžbinu
        Long orderId = orderRepository.createOrder(user.getId(), totalFinalPrice);

        // 3. Upis stavki i smanjenje zaliha
        for (CartItemRequest item : dto.getItems()) {
            Equipment eq = equipmentRepository.findById(item.getEquipment_id()).get();
            int discount = equipmentRepository.findActiveDiscountForSport(eq.getSport_type());

            double finalItemPrice = eq.getPrice() * (1 - discount / 100.0);

            // Upisujemo istorijske podatke u order_items
            orderRepository.saveOrderItem(orderId, eq.getId(), item.getQuantity(),
                    eq.getPrice(), discount, finalItemPrice);

            equipmentRepository.updateStock(eq.getId(), eq.getStock_quantity() - item.getQuantity());
        }
    }

    @Transactional
    public void cancelOrder(Long orderId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Korisnik nije pronađen."));

        String currentStatus = orderRepository.findStatusByIdAndUserId(orderId, user.getId());
        if (!"ORDERED".equals(currentStatus)) {
            throw new RuntimeException("Može se otkazati samo 'ORDERED' porudžbina.");
        }

        // Vrati zalihe: moramo naći stavke porudžbine
        List<OrderItem> items = orderRepository.findItemsByOrderId(orderId);
        for (OrderItem item : items) {
            equipmentRepository.incrementStock(item.getEquipment_id(), item.getQuantity());
        }

        orderRepository.updateStatus(orderId, user.getId(), "CANCELLED");
    }
}