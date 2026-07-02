package com.example.backend.dtos;

public class RatingRequest {
    private Boolean is_like;
    private String comment;

    public Boolean getIs_like() { return is_like; }
    public void setIs_like(Boolean is_like) { this.is_like = is_like; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
}