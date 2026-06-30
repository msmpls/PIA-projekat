package com.example.backend.models;

public class TeammateAdResponse {
    private Long id;
    private Long ad_id;
    private Long user_id;
    private String status; // "PENDING", "APPROVED", "REJECTED"

    public TeammateAdResponse() {}

    public TeammateAdResponse(Long id, Long ad_id, Long user_id, String status) {
        this.id = id;
        this.ad_id = ad_id;
        this.user_id = user_id;
        this.status = status;
    }

    // Getteri i Setteri
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getAd_id() { return ad_id; }
    public void setAd_id(Long ad_id) { this.ad_id = ad_id; }

    public Long getUser_id() { return user_id; }
    public void setUser_id(Long user_id) { this.user_id = user_id; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}