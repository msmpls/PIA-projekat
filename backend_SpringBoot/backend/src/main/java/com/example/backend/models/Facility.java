package com.example.backend.models;

import java.math.BigDecimal;

public class Facility {
    private Long id;
    private String name;
    private String city;
    private String address;
    private String description;
    private BigDecimal price_per_hour;
    private String type;
    private String status;
    private Long created_by;
    private String company_name;
    private String company_address;
    private String company_id_number;
    private String pib;
    private String work_start_time;
    private String work_end_time;

    public Facility() {}

    public Facility(Long id, String name, String city, String address, String description, BigDecimal price_per_hour, 
                    String type, String status, Long created_by, String company_name, String company_address, 
                    String company_id_number, String pib, String work_start_time, String work_end_time) {
        this.id = id;
        this.name = name;
        this.city = city;
        this.address = address;
        this.description = description;
        this.price_per_hour = price_per_hour;
        this.type = type;
        this.status = status;
        this.created_by = created_by;
        this.company_name = company_name;
        this.company_address = company_address;
        this.company_id_number = company_id_number;
        this.pib = pib;
        this.work_start_time = work_start_time;
        this.work_end_time = work_end_time;
    }

    // Getteri i Setteri
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getPrice_per_hour() { return price_per_hour; }
    public void setPrice_per_hour(BigDecimal price_per_hour) { this.price_per_hour = price_per_hour; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Long getCreated_by() { return created_by; }
    public void setCreated_by(Long created_by) { this.created_by = created_by; }

    public String getCompany_name() { return company_name; }
    public void setCompany_name(String company_name) { this.company_name = company_name; }

    public String getCompany_address() { return company_address; }
    public void setCompany_address(String company_address) { this.company_address = company_address; }

    public String getCompany_id_number() { return company_id_number; }
    public void setCompany_id_number(String company_id_number) { this.company_id_number = company_id_number; }

    public String getPib() { return pib; }
    public void setPib(String pib) { this.pib = pib; }

    public String getWork_start_time() { return work_start_time; }
    public void setWork_start_time(String work_start_time) { this.work_start_time = work_start_time; }

    public String getWork_end_time() { return work_end_time; }
    public void setWork_end_time(String work_end_time) { this.work_end_time = work_end_time; }
}