package com.example.backend.models;

public class Order {
    private Long id;
    private Long user_id;
    private String order_date;    // "YYYY-MM-DD HH:MM:SS" (MySQL DATETIME)
    private Double total_price;
    private String status;         // "ORDERED", "DELIVERED" "DECLINED"

    public Order() {}

    public Order(Long id, Long user_id, String order_date, Double total_price, String status) {
        this.id = id;
        this.user_id = user_id;
        this.order_date = order_date;
        this.total_price = total_price;
        this.status = status;
    }

    // Getteri i Setteri
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUser_id() { return user_id; }
    public void setUser_id(Long user_id) { this.user_id = user_id; }

    public String getOrder_date() { return order_date; }
    public void setOrder_date(String order_date) { this.order_date = order_date; }

    public Double getTotal_price() { return total_price; }
    public void setTotal_price(Double total_price) { this.total_price = total_price; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}