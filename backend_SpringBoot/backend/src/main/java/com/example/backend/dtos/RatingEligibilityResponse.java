package com.example.backend.dtos;

public class RatingEligibilityResponse {
    private int confirmedReservations;
    private int usedRatings;
    private boolean canRate;

    public RatingEligibilityResponse() {}

    public RatingEligibilityResponse(int confirmedReservations, int usedRatings, boolean canRate) {
        this.confirmedReservations = confirmedReservations;
        this.usedRatings = usedRatings;
        this.canRate = canRate;
    }

    public int getConfirmedReservations() { return confirmedReservations; }
    public void setConfirmedReservations(int confirmedReservations) { this.confirmedReservations = confirmedReservations; }

    public int getUsedRatings() { return usedRatings; }
    public void setUsedRatings(int usedRatings) { this.usedRatings = usedRatings; }

    public boolean isCanRate() { return canRate; }
    public void setCanRate(boolean canRate) { this.canRate = canRate; }
}