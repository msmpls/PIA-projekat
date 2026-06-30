package com.example.backend.services;

import com.example.backend.dtos.FacilityRequest;
import com.example.backend.dtos.FacilityRequest.CourtRequest;
import com.example.backend.models.Court;
import com.example.backend.models.Facility;
import com.example.backend.repos.CourtRepository;
import com.example.backend.repos.FacilityRepository;
import com.example.backend.repos.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FacilityService {

    private final FacilityRepository facilityRepository;
    private final CourtRepository courtRepository;
    private final UserRepository userRepository;

    public FacilityService(FacilityRepository facilityRepository, 
                           CourtRepository courtRepository, 
                           UserRepository userRepository) {
        this.facilityRepository = facilityRepository;
        this.courtRepository = courtRepository;
        this.userRepository = userRepository;
    }
    @Transactional
    public void createFacilityWithCourts(FacilityRequest request, Long employeeId) {
        
        // 1. VALIDACIJA: Provera biznis pravila
        boolean hasValidOpenCourt = false;
        if (request.getCourts() != null) {
            for (FacilityRequest.CourtRequest cr : request.getCourts()) {
                if ("OPENED".equalsIgnoreCase(cr.getType()) && cr.getCapacity() >= 4) {
                    hasValidOpenCourt = true;
                    break;
                }
            }
        }

        if (!hasValidOpenCourt) {
            throw new IllegalArgumentException("Objekat mora imati najmanje jedan otvoreni teren sa kapacitetom od 4 ili više mesta (N >= 4).");
        }

        // 2. KREIRANJE MODELA OBJEKTA I UPIS U BAZU (Sve je sa donjim crtama!)
        Facility facility = new Facility();
        facility.setName(request.getName());
        facility.setCity(request.getCity());
        facility.setAddress(request.getAddress());
        facility.setDescription(request.getDescription());
        facility.setPrice_per_hour(request.getPrice_per_hour()); 
        facility.setType(request.getType());
        facility.setStatus("PENDING"); 
        facility.setCreated_by(employeeId); 
        facility.setCompany_name(request.getCompany_name());
        facility.setCompany_address(request.getCompany_address());
        facility.setCompany_id_number(request.getCompany_id_number());
        facility.setPib(request.getPib());
        facility.setWork_start_time(request.getWork_start_time());
        facility.setWork_end_time(request.getWork_end_time());

        facilityRepository.save(facility);

        // 3. IZVLAČENJE GENERISANOG ID-JA OBJEKTA
        Long generatedFacilityId = userRepository.findFacilityIdByDetails(
                facility.getName(),
                facility.getCompany_id_number(), 
                facility.getPib(),
                facility.getCompany_address()    
        );

        if (generatedFacilityId == null) {
            throw new IllegalStateException("Greška pri kreiranju objekta. ID nije pronađen.");
        }

        // 4. KREIRANJE I UPIS SVIH TERENA U PETLJI
        for (FacilityRequest.CourtRequest cr : request.getCourts()) {
            Court court = new Court();
            court.setFacility_id(generatedFacilityId);
            court.setCourt_name(cr.getCourt_name());
            court.setSport_type(cr.getSport_type());
            court.setType(cr.getType());
            court.setCapacity(cr.getCapacity());
            court.setEquipment_description(cr.getEquipment_description());

            courtRepository.save(court);
        }
        
        // 5. VEZIVANJE ZAPOSLENOG ZA OBJEKAT U SPOJNOJ TABELI
        userRepository.saveFacilityEmployeeRelation(generatedFacilityId, facility.getCreated_by());
    }

    @Transactional
public void addCourtToExistingFacility(Long facilityId, CourtRequest request, Long employeeId) throws IllegalAccessException {
    
    // 1. BEZBEDNOST: Pita repozitorijum, nema SQL-a ovde
    if (!userRepository.isEmployeeAssignedToFacility(facilityId, employeeId)) {
        throw new IllegalAccessException("Nemate ovlašćenje da dodajete terene u ovaj objekat.");
    }

    // 2. VALIDACIJA: Dužina opisa opreme
    if (request.getEquipment_description() != null && request.getEquipment_description().length() > 300) {
        throw new IllegalArgumentException("Opis opreme ne sme prelaziti 300 karaktera.");
    }

    // 3. VALIDACIJA: Jedinstven naziv terena
    if (courtRepository.existsByFacilityIdAndCourtName(facilityId, request.getCourt_name())) {
        throw new IllegalArgumentException("Teren sa nazivom '" + request.getCourt_name() + "' već postoji u ovom objektu.");
    }

    // 4. MAPIRANJE I UPIS Preko CourtRepository-ja
    Court court = new Court();
    court.setFacility_id(facilityId);
    court.setCourt_name(request.getCourt_name());
    court.setSport_type(request.getSport_type());
    court.setType(request.getType());
    court.setCapacity(request.getCapacity());
    court.setEquipment_description(request.getEquipment_description());

    courtRepository.save(court);
}
    

}