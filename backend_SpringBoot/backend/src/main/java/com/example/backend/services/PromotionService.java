package com.example.backend.services;

import com.example.backend.models.Promotion;
import com.example.backend.repos.PromotionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PromotionService {

    private final PromotionRepository promotionRepository;

    public PromotionService(PromotionRepository promotionRepository) {
        this.promotionRepository = promotionRepository;
    }

    public List<Promotion> getAllPromotions() {
        return promotionRepository.findAll();
    }

    public void createPromotion(Promotion p) {
        if (p.getName() == null || p.getName().isBlank()) {
            throw new RuntimeException("Naziv promocije je obavezan.");
        }
        if (p.getDiscount_percent() == null || p.getDiscount_percent() < 1 || p.getDiscount_percent() > 100) {
            throw new RuntimeException("Popust mora biti između 1 i 100.");
        }
        if (p.getStart_date() == null || p.getEnd_date() == null) {
            throw new RuntimeException("Datum početka i kraja su obavezni.");
        }
        if (p.getEnd_date().isBefore(p.getStart_date())) {
            throw new RuntimeException("Datum kraja ne može biti pre datuma početka.");
        }
        promotionRepository.save(p);
    }

    public void updatePromotion(Long id, Promotion p) {
        if (p.getDiscount_percent() != null && (p.getDiscount_percent() < 1 || p.getDiscount_percent() > 100)) {
            throw new RuntimeException("Popust mora biti između 1 i 100.");
        }
        if (p.getStart_date() != null && p.getEnd_date() != null && p.getEnd_date().isBefore(p.getStart_date())) {
            throw new RuntimeException("Datum kraja ne može biti pre datuma početka.");
        }
        promotionRepository.update(id, p);
    }

    public void deletePromotion(Long id) {
        promotionRepository.delete(id);
    }
}
