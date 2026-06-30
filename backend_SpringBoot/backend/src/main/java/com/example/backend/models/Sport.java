package com.example.backend.models;

public class Sport {
    private Long id;
    private String name;
    private String description;

    // Default konstruktor
    public Sport() {}

    // Konstruktor sa parametrima
    public Sport(Long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    // Getteri i Setteri
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}