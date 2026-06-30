package com.example.backend.models;

public class User {

    private Long id;
    private String username;
    private String password_hash;
    private String email;
    private String first_name;           // Poklapa se sa kolonom first_name
    private String last_name;            // Poklapa se sa kolonom last_name
    private String phone;
    private String role;                 // "ATHLETE", "EMPLOYEE", "ADMIN"
    private String status;               // "PENDING", "APPROVED", "REJECTED"
    private String profile_picture_path; // Poklapa se sa kolonom profile_picture_path
    private String favorite_sports;      // Poklapa se sa kolonom favorite_sports

    public User() {
    }

    public User(Long id, String username, String password_hash, String email, String first_name,
                String last_name, String phone, String role, String status,
                String profile_picture_path, String favorite_sports) {
        this.id = id;
        this.username = username;
        this.password_hash = password_hash;
        this.email = email;
        this.first_name = first_name;
        this.last_name = last_name;
        this.phone = phone;
        this.role = role;
        this.status = status;
        this.profile_picture_path = profile_picture_path;
        this.favorite_sports = favorite_sports;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword_hash() { return password_hash; }
    public void setPassword_hash(String password_hash) { this.password_hash = password_hash; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFirst_name() { return first_name; }
    public void setFirst_name(String first_name) { this.first_name = first_name; }

    public String getLast_name() { return last_name; }
    public void setLast_name(String last_name) { this.last_name = last_name; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getProfile_picture_path() { return profile_picture_path; }
    public void setProfile_picture_path(String profile_picture_path) { this.profile_picture_path = profile_picture_path; }

    public String getFavorite_sports() { return favorite_sports; }
    public void setFavorite_sports(String favorite_sports) { this.favorite_sports = favorite_sports; }
}