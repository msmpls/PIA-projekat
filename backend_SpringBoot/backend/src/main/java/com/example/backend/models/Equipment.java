package com.example.backend.models;

public class Equipment {
    private Long id;
    private String name;
    private String image_path;
    private String sport_type;
    private Double price;
    private Integer stock_quantity;
    private String description;

    public Equipment() {}

    public Equipment(Long id, String name,String image_path,String sport_type,Double price, Integer stock_quantity, String description) {
        this.id = id;
        this.name = name;
        this.image_path = image_path;
        this.sport_type = sport_type; 
        this.price = price;
        this.stock_quantity = stock_quantity;
        this.description = description;
    }

    public String getImage_path() {return image_path;}
    public void setImage_path(String image_path) {this.image_path = image_path;}

    public String getSport_type() {return sport_type;}
    public void setSport_type(String sport_type) {this.sport_type = sport_type;}

    // Getteri i Setteri
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public Integer getStock_quantity() { return stock_quantity; }
    public void setStock_quantity(Integer stock_quantity) { this.stock_quantity = stock_quantity; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}