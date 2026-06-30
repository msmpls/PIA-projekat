package com.example.backend.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern; // DODAJ OVAJ IMPORT
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "Korisničko ime je obavezno.")
    @Size(min = 3, max = 50, message = "Korisničko ime mora biti između 3 i 50 karaktera.")
    private String username;

    @NotBlank(message = "Lozinka je obavezna.")
    // ZAMENI @Size OVIM STROGIM REGEX-OM KAO U LOGIN REQUESTU:
    @Pattern(
        regexp = "^(?=.*[A-Z])(?=.*\\d)(?=.*[^a-zA-Z0-9])[a-zA-Z].{7,11}$", 
        message = "Lozinka mora imati 8-12 karaktera, počinjati slovom, i sadržati bar jedno veliko slovo, jedan broj i jedan specijalni karakter."
    )
    private String password;

    @NotBlank(message = "Email je obavezan.")
    @Email(message = "Email adresa mora biti u ispravnom formatu.")
    private String email;

    @NotBlank(message = "Ime je obavezno.")
    private String first_name;

    @NotBlank(message = "Prezime je obavezno.")
    private String last_name;

    private String phone;

    @NotBlank(message = "Uloga je obavezna.")
    private String role; // "ATHLETE" ili "EMPLOYEE"

    private String favorite_sports;

    private MultipartFile profile_image; 

    private String facility_name;     // naziv sportskog objekta (facilities.name)
    
    private String company_address;   // adresa sedišta firme (facilities.company_address)

    @Pattern(regexp = "^\\d{8}$", message = "Matični broj firme mora imati tačno 8 cifara.")
    private String company_id_number; // matični broj firme (facilities.company_id_number)

    @Pattern(regexp = "^[1-9]\\d{8}$", message = "PIB mora imati tačno 9 cifara i ne sme počinjati nulom.")
    private String pib;               // porez id broj firme

}