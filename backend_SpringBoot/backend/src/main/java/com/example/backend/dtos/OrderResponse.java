package com.example.backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    private Long id;
    private String order_date;
    private Double total_price; // Ovo je finalna cena koju je korisnik platio
    private String status;
    private String items_summary;
    private Double total_original_price; // Dodato: da vidi koliko bi koštalo bez popusta
}