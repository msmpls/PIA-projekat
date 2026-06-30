package com.example.backend.models;

public class OrderItem {
    private Long id;
    private Long order_id;
    private Long equipment_id;
    private Integer quantity;
    
    // Nova polja za istorijsku tačnost
    private Double unit_price;       // Cena u trenutku kupovine
    private Integer discount_percent; // Popust primenjen u trenutku kupovine
    private Double final_price;       // Konačna cena koju je kupac platio

    public OrderItem() {}

    // Getteri i Setteri
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getOrder_id() { return order_id; }
    public void setOrder_id(Long order_id) { this.order_id = order_id; }

    public Long getEquipment_id() { return equipment_id; }
    public void setEquipment_id(Long equipment_id) { this.equipment_id = equipment_id; }
    
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public Double getUnit_price() { return unit_price; }
    public void setUnit_price(Double unit_price) { this.unit_price = unit_price; }

    public Integer getDiscount_percent() { return discount_percent; }
    public void setDiscount_percent(Integer discount_percent) { this.discount_percent = discount_percent; }

    public Double getFinal_price() { return final_price; }
    public void setFinal_price(Double final_price) { this.final_price = final_price; }
}