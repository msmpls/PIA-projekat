package com.example.backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminUserResponse {
    private Long id;
    private String username;
    private String email;
    private String first_name;
    private String last_name;
    private String phone;
    private String role;
    private String status;
    private String profile_picture_path;
    private String favorite_sports;
}
