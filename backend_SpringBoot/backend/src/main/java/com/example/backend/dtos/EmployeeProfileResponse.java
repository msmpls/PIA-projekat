package com.example.backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeProfileResponse {
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private String role;
    private String status;
    private String profilePicturePath;
    
    // Umesto List<Map>, koristimo listu ugnježdenih objekata
    private List<EmployeeFacilityDTO> myFacilities;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EmployeeFacilityDTO {
        private Long id;
        private String name;
        private String city;
        private String address;
        private String description;
        private Double pricePerHour;
        private String type;
        private String status;
        private String companyName;
        private String companyAddress;
        private String companyIdNumber;
        private String pib;
    }
}