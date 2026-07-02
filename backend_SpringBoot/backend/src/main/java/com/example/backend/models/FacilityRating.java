package com.example.backend.models;

import java.time.LocalDateTime;

public class FacilityRating {
    private Long id;
    private Long facility_id;
    private Long user_id;
    private Boolean is_like;
    private String comment;
    private LocalDateTime created_at;

    public FacilityRating() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getFacility_id() { return facility_id; }
    public void setFacility_id(Long facility_id) { this.facility_id = facility_id; }

    public Long getUser_id() { return user_id; }
    public void setUser_id(Long user_id) { this.user_id = user_id; }

    public Boolean getIs_like() { return is_like; }
    public void setIs_like(Boolean is_like) { this.is_like = is_like; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public LocalDateTime getCreated_at() { return created_at; }
    public void setCreated_at(LocalDateTime created_at) { this.created_at = created_at; }
}