package com.example.backend.services;

import com.example.backend.dtos.AuthResponse;
import com.example.backend.dtos.EmployeeProfileResponse;
import com.example.backend.dtos.LoginRequest;
import com.example.backend.dtos.RegisterRequest;
import com.example.backend.models.User;
import com.example.backend.repos.UserRepository;
import com.example.backend.security.JwtUtil;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    // Konstruktorska injekcija svih potrebnih komponenti
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    /**
     * BIZNIS LOGIKA ZA REGISTRACIJU
     */
    private final String UPLOAD_DIR = "uploads/profiles/";

    /*
     * @Transactional
     * public AuthResponse register(RegisterRequest request) {
     * // 1. Provera jedinstvenosti korisničkog imena
     * if (userRepository.findByUsername(request.getUsername()).isPresent()) {
     * throw new RuntimeException("Korisničko ime '" + request.getUsername() +
     * "' je već zauzeto.");
     * }
     * 
     * // 2. Prepakivanje (mapiranje) iz DTO-a u Model
     * User user = new User();
     * user.setUsername(request.getUsername());
     * user.setEmail(request.getEmail());
     * user.setFirst_name(request.getFirst_name());
     * user.setLast_name(request.getLast_name());
     * user.setPhone(request.getPhone());
     * user.setRole(request.getRole());
     * user.setFavorite_sports(request.getFavorite_sports());
     * user.setStatus("PENDING");
     * 
     * // ---- OBRADA PROFILNE SLIKE ----
     * MultipartFile file = request.getProfile_image();
     * if (file != null && !file.isEmpty()) {
     * try {
     * // Kreiranje uploads foldera ako ne postoji
     * File uploadDir = new File(UPLOAD_DIR);
     * if (!uploadDir.exists()) {
     * uploadDir.mkdirs();
     * }
     * 
     * // Izvlačenje ekstenzije (.jpg, .png...)
     * String originalFileName = file.getOriginalFilename();
     * String extension = originalFileName != null
     * ? originalFileName.substring(originalFileName.lastIndexOf("."))
     * : ".jpg";
     * 
     * // Unikatno ime fajla
     * String uniqueFileName = "profile_" + request.getUsername() + "_"
     * + UUID.randomUUID().toString().substring(0, 8) + extension;
     * Path targetPath = Paths.get(UPLOAD_DIR + uniqueFileName);
     * 
     * // Snimanje fajla na disk
     * Files.copy(file.getInputStream(), targetPath,
     * StandardCopyOption.REPLACE_EXISTING);
     * 
     * // Putanja koju pišemo u bazu
     * String savedImagePath = "/uploads/" + uniqueFileName;
     * user.setProfile_picture_path(savedImagePath);
     * 
     * } catch (Exception e) {
     * throw new RuntimeException("Greška pri čuvanju profilne slike: " +
     * e.getMessage());
     * }
     * } else {
     * // Opciono: ako korisnik nije dodao sliku, stavi null ili putanju do neke
     * // podrazumevane (default) slike
     * user.setProfile_picture_path(null);
     * }
     * // --------------------------------
     * 
     * // 3. Sigurnost: Heširanje lozinke pre upisa u bazu
     * String hashedPassword = passwordEncoder.encode(request.getPassword());
     * user.setPassword_hash(hashedPassword);
     * 
     * // 4. Upis u MySQL preko repozitorijuma
     * userRepository.save(user);
     * 
     * // 5. Automatsko generisanje tokena odmah nakon uspešne registracije
     * String token = jwtUtil.generateToken(user.getUsername(), user.getRole());
     * 
     * return new AuthResponse(token, user.getUsername(), user.getRole(),
     * user.getStatus());
     * }
     */
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // 1. Provera jedinstvenosti korisničkog imena preko postojećeg repoa
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Korisničko ime '" + request.getUsername() + "' je već zauzeto.");
        }

        // --- PRE-VALIDACIJA ZA ULOGU ZAPOSLENOG (PREKO REPO METODA) ---
        Long targetFacilityId = null;
        if ("EMPLOYEE".equals(request.getRole())) {

            // Pozivamo repo da nađe objekat po 4 parametra
            targetFacilityId = userRepository.findFacilityIdByDetails(
                    request.getFacility_name(),
                    request.getCompany_id_number(),
                    request.getPib(),
                    request.getCompany_address());

            // Ako objekat ne postoji, odmah prekidamo registraciju
            if (targetFacilityId == null) {
                throw new RuntimeException(
                        "Registracija odbijena: Sportski objekat sa unetim podacima nije pronađen u sistemu.");
            }

            // Pozivamo repo da prebroji trenutne radnike za taj objekat
            int employeeCount = userRepository.countEmployeesForFacility(targetFacilityId);
            if (employeeCount >= 2) {
                throw new RuntimeException(
                        "Registracija odbijena: Ovaj objekat već ima maksimalan broj zaposlenih (2).");
            }
        }
        // -----------------------------------------------------------------

        // 2. Prepakivanje (mapiranje) iz DTO-a u Model
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setFirst_name(request.getFirst_name());
        user.setLast_name(request.getLast_name());
        user.setPhone(request.getPhone());
        user.setRole(request.getRole());

        if ("ATHLETE".equals(request.getRole())) {
            user.setFavorite_sports(request.getFavorite_sports());
        } else {
            user.setFavorite_sports(null);
        }
        user.setStatus("PENDING");

        // obrada profilne slike
        MultipartFile file = request.getProfile_image();
        if (file != null && !file.isEmpty()) {
            try {
                File uploadDir = new File(UPLOAD_DIR);
                if (!uploadDir.exists()) {
                    uploadDir.mkdirs();
                }

                String originalFileName = file.getOriginalFilename();
                String extension = originalFileName != null
                        ? originalFileName.substring(originalFileName.lastIndexOf("."))
                        : ".jpg";

                String uniqueFileName = "profile_" + request.getUsername() + "_"
                        + UUID.randomUUID().toString().substring(0, 8) + extension;
                Path targetPath = Paths.get(UPLOAD_DIR + uniqueFileName);

                Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

                String savedImagePath = "/uploads/" + uniqueFileName;
                user.setProfile_picture_path(savedImagePath);

            } catch (Exception e) {
                throw new RuntimeException("Greška pri čuvanju profilne slike: " + e.getMessage());
            }
        } else {
            user.setProfile_picture_path(null);
        }
        // -----------------------------------------------------

        // 3. Sigurnost: Heširanje lozinke
        String hashedPassword = passwordEncoder.encode(request.getPassword());
        user.setPassword_hash(hashedPassword);

        // korisnika upisujemo u tabelu
        userRepository.save(user);

        User savedUser = userRepository.findByUsername(user.getUsername())
                .orElseThrow(() -> new RuntimeException("Greška pri registraciji: Korisnik nije uspešno sačuvan."));

        // upis u tabelu konekcija- ko gde radi - koristimo gore pronadjen targertfacility gde hoce da se prijavi
        if ("EMPLOYEE".equals(request.getRole()) && targetFacilityId != null) {
            userRepository.saveFacilityEmployeeRelation(targetFacilityId, savedUser.getId());
        }
        // ----------------------------------------------

        String token = jwtUtil.generateToken(savedUser.getUsername(), savedUser.getRole());

        return new AuthResponse(token, savedUser.getUsername(), savedUser.getRole(), savedUser.getStatus());
    }

    public EmployeeProfileResponse getEmployeeProfileData(String username) {
        // 1. Dobijamo čistog korisnika iz baze
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Zaposleni nije pronađen."));

        // 2. Pozivamo repo metod koji vraća strogo tipiziranu listu objekata
        List<EmployeeProfileResponse.EmployeeFacilityDTO> myFacilities = userRepository
                .findFacilitiesByUserId(user.getId());

        // 3. Pakujemo sve direktno u DTO odgovor
        return new EmployeeProfileResponse(
                user.getUsername(),
                user.getEmail(),
                user.getFirst_name(),
                user.getLast_name(),
                user.getPhone(),
                user.getRole(),
                user.getStatus(),
                user.getProfile_picture_path() != null ? user.getProfile_picture_path() : "",
                myFacilities);
    }

    //LOGIN
    public AuthResponse login(LoginRequest request) {
        // 1. Traženje korisnika u bazi. Ako ne postoji, bacamo izuzetak.
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BadCredentialsException("Neispravno korisničko ime ili lozinka."));

        // 2. Provera lozinke: matches() uzima čistu lozinku sa fronta i poredi je sa
        // hešom iz baze
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword_hash())) {
            throw new BadCredentialsException("Neispravno korisničko ime ili lozinka.");
        }

        // 2.1. Provera statusa korisnika: samo odobreni nalog može da se uloguje
        if (!"APPROVED".equalsIgnoreCase(user.getStatus())) {
            throw new BadCredentialsException("Korisnički nalog nije odobren.");
        }

        // 3. Ako je lozinka tačna, pravimo novi JWT token
        String token = jwtUtil.generateToken(user.getUsername(), user.getRole());

        // 4. Pakujemo podatke u AuthResponse i šaljemo nazad kontroleru
        return new AuthResponse(token, user.getUsername(), user.getRole(), user.getStatus());
    }

    @Transactional
    public void updateProfileDetails(String username, String firstName, String lastName, String phone, String email) {
        // izvučemo trenutne podatke iz baze
        User currentDetails = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Korisnik nije pronađen."));

        // ako je frontend poslao novo ime, koristimo njega if not korisstimo staro
        String updatedFirstName = (firstName != null && !firstName.trim().isEmpty()) ? firstName
                : currentDetails.getFirst_name();

        // Isto uradimo i za ostala polja
        String updatedLastName = (lastName != null && !lastName.trim().isEmpty()) ? lastName
                : currentDetails.getLast_name();
        String updatedPhone = (phone != null && !phone.trim().isEmpty()) ? phone : currentDetails.getPhone();
        String updatedEmail = (email != null && !email.trim().isEmpty()) ? email : currentDetails.getEmail();

        // pozivamo repo sa zeljenim izmenama i sa starim podacima gde nam treba-malo ne
        // eficentt but oke
        userRepository.updateProfile(username, updatedFirstName, updatedLastName, updatedPhone, updatedEmail);
    }

    @Transactional
    public void updateSports(String username, String favoriteSports) {
        userRepository.updateFavoriteSports(username, favoriteSports);
    }

    @Transactional
    public String updateImage(String username, MultipartFile file) {
        if (file.isEmpty()) {
            throw new RuntimeException("Fajl je prazan!");
        }

        try {
            // 1. Kreiramo uploads folder ako već ne postoji
            File uploadDir = new File(UPLOAD_DIR);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            // 2. Uzimamo originalnu ekstenziju (.jpg, .png...)
            String originalFileName = file.getOriginalFilename();
            String extension = originalFileName.substring(originalFileName.lastIndexOf("."));

            // 3. Pravimo unikatno ime fajla da se slike ne bi pregazile (npr.
            // profile_noviunikatko_3a2b.jpg)
            String uniqueFileName = "profile_" + username + "_" + UUID.randomUUID().toString().substring(0, 8)
                    + extension;

            // 4. Definišemo konačnu putanju gde se fajl snima na disku
            Path targetPath = Paths.get(UPLOAD_DIR + uniqueFileName);

            // 5. Fizički kopiramo fajl sa mreže u naš folder na serveru
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            // 6. Putanja koju ćemo upisati u bazu i koju će Angular koristiti da prikaže
            // sliku
            String imagePath = "/uploads/" + uniqueFileName;

            // 7. Upisujemo taj string u bazu podataka
            userRepository.updateProfileImage(username, imagePath);

            // Vraćamo putanju kontroleru ako nam zatreba
            return imagePath;

        } catch (Exception e) {
            throw new RuntimeException("Greška pri čuvanju slike na serveru: " + e.getMessage());
        }
    }
}