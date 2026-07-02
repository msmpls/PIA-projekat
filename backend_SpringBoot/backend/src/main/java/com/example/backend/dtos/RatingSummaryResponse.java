package com.example.backend.dtos;

import java.util.List;

public class RatingSummaryResponse {
    private long likes;
    private long dislikes;
    private List<RatingResponse> lastComments;

    public RatingSummaryResponse() {}

    public RatingSummaryResponse(long likes, long dislikes, List<RatingResponse> lastComments) {
        this.likes = likes;
        this.dislikes = dislikes;
        this.lastComments = lastComments;
    }

    public long getLikes() { return likes; }
    public void setLikes(long likes) { this.likes = likes; }

    public long getDislikes() { return dislikes; }
    public void setDislikes(long dislikes) { this.dislikes = dislikes; }

    public List<RatingResponse> getLastComments() { return lastComments; }
    public void setLastComments(List<RatingResponse> lastComments) { this.lastComments = lastComments; }
}