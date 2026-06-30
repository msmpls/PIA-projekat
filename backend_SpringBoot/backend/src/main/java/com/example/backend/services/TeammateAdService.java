package com.example.backend.services;

import com.example.backend.dtos.TeammateAdDTOs.*;
import com.example.backend.models.User;
import com.example.backend.repos.TeammateAdRepository;
import com.example.backend.repos.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class TeammateAdService {

    private final TeammateAdRepository adRepository;
    private final UserRepository userRepository;

    public TeammateAdService(TeammateAdRepository adRepository, UserRepository userRepository) {
        this.adRepository = adRepository;
        this.userRepository = userRepository;
    }

    // 1. Kreiranje novog oglasa
    public void createAd(AdRequest dto, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Korisnik nije pronađen."));
        
        adRepository.saveAd(user.getId(), dto.getSport(), dto.getCity(), 
                            dto.getEvent_date(), dto.getEvent_time(), dto.getPlayers_needed());
    }

   public List<AdResponse> getActiveAds(String username) {
    // DODAJ OVE ISPISE:
    System.out.println("[SERVIS] getActiveAds pokrenut za username: '" + username + "'");
    
    System.out.println("[SERVIS] Pokrećem userRepository.findByUsername('" + username + "')...");
    
    User user = userRepository.findByUsername(username)
            .orElseThrow(() -> {
                System.out.println("[SERVIS ERROR] Baza je vratila prazan rezultat za: '" + username + "'!");
                return new RuntimeException("Korisnik nije pronađen.");
            });
            
    System.out.println("[SERVIS] Korisnik uspešno pronađen u bazi! Njegov ID je: " + user.getId());
    
    return adRepository.findActiveAdsExcludingUser(user.getId());
}

    // 3. Prijava na oglas saigrača
    public void respondToAd(Long adId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Korisnik nije pronađen."));

        Map<String, Object> ad = adRepository.findAdById(adId);
        if (ad == null) {
            throw new RuntimeException("Oglas ne postoji.");
        }

        Long creatorId = ((Number) ad.get("creator_id")).longValue();
        String status = (String) ad.get("status");

        if (!"OPEN".equals(status)) {
            throw new RuntimeException("Ovaj oglas je zatvoren.");
        }

        if (user.getId().equals(creatorId)) {
            throw new RuntimeException("Ne možete se prijaviti na sopstveni oglas.");
        }

        if (adRepository.hasUserAlreadyResponded(adId, user.getId())) {
            throw new RuntimeException("Već ste poslali zahtev za ovaj oglas.");
        }

        adRepository.saveResponse(adId, user.getId());
    }

    // 4. Moji oglasi na profilu (sa svim prijavama za svaki oglas)
    public List<AdResponseDetails> getMyAdsWithResponses(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Korisnik nije pronađen."));

        List<AdResponse> myRawAds = adRepository.findAdsByCreatorId(user.getId());
        List<AdResponseDetails> detailedList = new ArrayList<>();

        for (AdResponse ad : myRawAds) {
            AdResponseDetails details = new AdResponseDetails();
            details.setId(ad.getId());
            details.setSport(ad.getSport());
            details.setCity(ad.getCity());
            details.setEvent_date(ad.getEvent_date());
            details.setEvent_time(ad.getEvent_time());
            details.setPlayers_needed(ad.getPlayers_needed());
            details.setStatus(ad.getStatus());
            
            // Povuci sve ljude koji su se prijavili za ovaj konkretan oglas
            List<TeammateResponseDTO> responses = adRepository.findResponsesByAdId(ad.getId());
            details.setResponses(responses);

            detailedList.add(details);
        }

        return detailedList;
    }

    // 5. Obrada prijave: Odobravanje ili Odbijanje
    @Transactional
    public void handleResponseStatus(Long responseId, String newStatus, String username) {
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Korisnik nije pronađen."));

        // Nalazimo podatke o oglasu na osnovu ID-ja prijave
        Map<String, Object> adInfo = adRepository.findAdInfoByResponseId(responseId);
        if (adInfo == null) {
            throw new RuntimeException("Prijava ili oglas nisu pronađeni.");
        }

        Long creatorId = ((Number) adInfo.get("creator_id")).longValue();
        Long adId = ((Number) adInfo.get("ad_id")).longValue();
        int playersNeeded = ((Number) adInfo.get("players_needed")).intValue();

        // Bezbednosna provera: Da li ulogovani korisnik stvarno vlasnik oglasa?
        if (!currentUser.getId().equals(creatorId)) {
            throw new RuntimeException("Nemate dozvolu da upravljate prijavama za ovaj oglas.");
        }

        if (!"APPROVED".equals(newStatus) && !"REJECTED".equals(newStatus)) {
            throw new RuntimeException("Nevalidan status. Dozvoljeno je samo 'APPROVED' ili 'REJECTED'.");
        }

        // Ažuriramo status pojedinačne prijave
        adRepository.updateResponseStatus(responseId, newStatus);

        // Ako je prijava ODOBRENA, smanjujemo broj potrebnih igrača
        if ("APPROVED".equals(newStatus)) {
            if (playersNeeded <= 0) {
                throw new RuntimeException("Ekipa je već kompletirana.");
            }

            adRepository.decrementPlayersNeeded(adId);
            playersNeeded--; // Smanjujemo i lokalnu promenljivu za proveru ispod

            // Ako je ekipa kompletirana (dostignuta nula), automatski zatvaramo oglas
            if (playersNeeded == 0) {
                adRepository.updateAdStatus(adId, "CLOSED");
            }
        }
    }

    // 6. Ručno zatvaranje oglasa ("Zatvori oglas" dugme)
    public void closeAdManually(Long adId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Korisnik nije pronađen."));

        Map<String, Object> ad = adRepository.findAdById(adId);
        Long creatorId = ((Number) ad.get("creator_id")).longValue();

        if (!user.getId().equals(creatorId)) {
            throw new RuntimeException("Možete zatvoriti samo sopstvene oglase.");
        }

        adRepository.updateAdStatus(adId, "CLOSED");
    }
}