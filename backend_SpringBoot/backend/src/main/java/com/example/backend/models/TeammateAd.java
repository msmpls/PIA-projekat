package com.example.backend.models;

public class TeammateAd {
    private Long id;
    private Long creator_id;
    private String sport;
    private String city;
    private String event_date;    // "YYYY-MM-DD"
    private String event_time;    // "HH:MM:SS"
    private Integer players_needed;
    private String status;        // "OPEN", "CLOSED"

    public TeammateAd() {}

    public TeammateAd(Long id, Long creator_id, String sport, String city, 
                      String event_date, String event_time, Integer players_needed, String status) {
        this.id = id;
        this.creator_id = creator_id;
        this.sport = sport;
        this.city = city;
        this.event_date = event_date;
        this.event_time = event_time;
        this.players_needed = players_needed;
        this.status = status;
    }

    // Getteri i Setteri
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getCreator_id() { return creator_id; }
    public void setCreator_id(Long creator_id) { this.creator_id = creator_id; }

    public String getSport() { return sport; }
    public void setSport(String sport) { this.sport = sport; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getEvent_date() { return event_date; }
    public void setEvent_date(String event_date) { this.event_date = event_date; }

    public String getEvent_time() { return event_time; }
    public void setEvent_time(String event_time) { this.event_time = event_time; }

    public Integer getPlayers_needed() { return players_needed; }
    public void setPlayers_needed(Integer players_needed) { this.players_needed = players_needed; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}