package com.example.backend.models;

public class Court {
    private Long id;
    private Long facility_id;   
    private String court_name;  
    private String sport_type;  
    private String type;        // "OPENED" ili "CLOSED"
    private int capacity;       // N broj mesta
    private String equipment_description; // do 300 karaktera

    public Court() {}

    public Court(Long id, Long facility_id, String court_name, String sport_type, String type, int capacity, String equipment_description) {
        this.id = id;
        this.facility_id = facility_id;
        this.court_name = court_name;
        this.sport_type = sport_type;
        this.type = type;
        this.capacity = capacity;
        this.equipment_description = equipment_description;
    }

    // Tvoji postojeći Getteri i Setteri...
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getFacility_id() { return facility_id; }
    public void setFacility_id(Long facility_id) { this.facility_id = facility_id; }

    public String getCourt_name() { return court_name; }
    public void setCourt_name(String court_name) { this.court_name = court_name; }
    
    public String getSport_type() { return sport_type; }
    public void setSport_type(String sport_type) { this.sport_type = sport_type; }

    public String getType(){ return this.type; }
    public void setType(String type) {this.type = type;}

    // Novi Getteri i Setteri
    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    public String getEquipment_description() { return equipment_description; }
    public void setEquipment_description(String equipment_description) { this.equipment_description = equipment_description; }
}