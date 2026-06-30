package com.example.backend.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    @NotBlank(message = "Korisničko ime ne sme biti prazno.")
    private String username;

    @NotBlank(message = "Lozinka je obavezna.")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*\\d)(?=.*[^a-zA-Z0-9])[a-zA-Z].{7,11}$", message = "Lozinka mora imati 8-12 karaktera, počinjati slovom, i sadržati bar jedno veliko slovo, jedan broj i jedan specijalni karakter.")
    private String password;
}