package com.example.backend.controllers;

import com.example.backend.dtos.AuthResponse;
import com.example.backend.dtos.LoginRequest;
import com.example.backend.dtos.RegisterRequest;
import com.example.backend.services.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.http.MediaType;


@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:4200") // Dozvoljava Angularu da pristupi bekndu
public class AuthController {

    private final UserService userService;

    // Konstruktorska injekcija servisa
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    
    //RUTA ZA LOGIN: POST http://localhost:8080/api/auth/login 
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login( @Valid @RequestBody LoginRequest request) { 
        // 1. Pozivamo servis da odradi proveru i generiše token
        AuthResponse response = userService.login(request);
        
        // 2. Vraćamo HTTP status 200 OK zajedno sa AuthResponse objektom u telu (body)
        return ResponseEntity.ok(response);
    }

    
    //RUTA ZA REGISTRACIJU: POST http://localhost:8080/api/auth/register
  

    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
public ResponseEntity<AuthResponse> register(@Valid @ModelAttribute RegisterRequest request) {
    AuthResponse response = userService.register(request);
    return ResponseEntity.ok(response);
}
}