package com.example.backend.models;

public class Coach {
    private Long id;
    private Long facility_id;
    private String name;
    private String specialization;
    private String sport;
    private Double price_per_hour;
    private Double average_rating;
    private Boolean is_active;

    public Coach() {}

    public Coach(Long id, Long facility_id, String name, String specialization,
                 String sport, Double price_per_hour, Double average_rating, Boolean is_active) {
        this.id = id;
        this.facility_id = facility_id;
        this.name = name;
        this.specialization = specialization;
        this.sport = sport;
        this.price_per_hour = price_per_hour;
        this.average_rating = average_rating;
        this.is_active = is_active;
    }

    // Getteri i Setteri
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getFacility_id() { return facility_id; }
    public void setFacility_id(Long facility_id) { this.facility_id = facility_id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }

    public String getSport() { return sport; }
    public void setSport(String sport) { this.sport = sport; }

    public Double getPrice_per_hour() { return price_per_hour; }
    public void setPrice_per_hour(Double price_per_hour) { this.price_per_hour = price_per_hour; }

    public Double getAverage_rating() { return average_rating; }
    public void setAverage_rating(Double average_rating) { this.average_rating = average_rating; }

    public Boolean getIs_active() { return is_active; }
    public void setIs_active(Boolean is_active) { this.is_active = is_active; }
}