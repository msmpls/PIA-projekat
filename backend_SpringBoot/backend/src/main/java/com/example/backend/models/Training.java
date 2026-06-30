package com.example.backend.models;

public class Training {
    private Long id;
    private Long user_id;
    private Long coach_id;
    private Long court_id;
    private String training_date; // Formira se kao "YYYY-MM-DD"
    private String start_time;    // Formira se kao "HH:MM:SS"
    private String end_time;      // Formira se kao "HH:MM:SS"
    private String status;        // "SCHEDULED", "COMPLETED", "CANCELLED"

    public Training() {}

    public Training(Long id, Long user_id, Long coach_id,Long court_id, String training_date, 
                    String start_time, String end_time, String status) {
        this.id = id;
        this.user_id = user_id;
        this.coach_id = coach_id;
        this.court_id = court_id;
        this.training_date = training_date;
        this.start_time = start_time;
        this.end_time = end_time;
        this.status = status;
    }

    // Getteri i Setteri
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUser_id() { return user_id; }
    public void setUser_id(Long user_id) { this.user_id = user_id; }

    public Long getCoach_id() { return coach_id; }
    public void setCoach_id(Long coach_id) { this.coach_id = coach_id; }

    public Long getCourt_id(){ return court_id; }
    public void setCourt_id(Long court_id) { this.court_id = court_id; }

    public String getTraining_date() { return training_date; }
    public void setTraining_date(String training_date) { this.training_date = training_date; }

    public String getStart_time() { return start_time; }
    public void setStart_time(String start_time) { this.start_time = start_time; }

    public String getEnd_time() { return end_time; }
    public void setEnd_time(String end_time) { this.end_time = end_time; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }


}