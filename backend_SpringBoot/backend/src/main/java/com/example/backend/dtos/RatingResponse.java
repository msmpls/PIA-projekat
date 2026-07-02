package com.example.backend.dtos;

import java.time.LocalDateTime;

public class RatingResponse {
    private Long id;
    private String username;
    private Boolean is_like;
    private String comment;
    private LocalDateTime created_at;

    public RatingResponse() {}

    public RatingResponse(Long id, String username, Boolean is_like, String comment, LocalDateTime created_at) {
        this.id = id;
        this.username = username;
        this.is_like = is_like;
        this.comment = comment;
        this.created_at = created_at;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public Boolean getIs_like() { return is_like; }
    public void setIs_like(Boolean is_like) { this.is_like = is_like; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public LocalDateTime getCreated_at() { return created_at; }
    public void setCreated_at(LocalDateTime created_at) { this.created_at = created_at; }
}