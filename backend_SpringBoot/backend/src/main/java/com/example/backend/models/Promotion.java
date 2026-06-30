package com.example.backend.models;

import java.time.LocalDate;

public class Promotion {
    private Long id;
    private String name;
    private Integer discount_percent;
    private String sport_type;
    private LocalDate start_date;
    private LocalDate end_date;
    private Boolean is_active;

    public Promotion() {}

    public Promotion(Long id, String name, Integer discount_percent, String sport_type, 
                     LocalDate start_date, LocalDate end_date, Boolean is_active) {
        this.id = id;
        this.name = name;
        this.discount_percent = discount_percent;
        this.sport_type = sport_type;
        this.start_date = start_date;
        this.end_date = end_date;
        this.is_active = is_active;
    }

    // Getteri i Setteri
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getDiscount_percent() { return discount_percent; }
    public void setDiscount_percent(Integer discount_percent) { this.discount_percent = discount_percent; }

    public String getSport_type() { return sport_type; }
    public void setSport_type(String sport_type) { this.sport_type = sport_type; }

    public LocalDate getStart_date() { return start_date; }
    public void setStart_date(LocalDate start_date) { this.start_date = start_date; }

    public LocalDate getEnd_date() { return end_date; }
    public void setEnd_date(LocalDate end_date) { this.end_date = end_date; }

    public Boolean getIs_active() { return is_active; }
    public void setIs_active(Boolean is_active) { this.is_active = is_active; }
}