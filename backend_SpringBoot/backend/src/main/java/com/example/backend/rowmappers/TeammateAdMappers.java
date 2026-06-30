package com.example.backend.rowmappers;

import com.example.backend.dtos.TeammateAdDTOs.AdResponse;
import com.example.backend.dtos.TeammateAdDTOs.TeammateResponseDTO;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;
 

public class TeammateAdMappers {

    // ====================================================================
    // 1. MAPERI ZA ČISTE MODELE (Preslikavanje 1-na-1 sa tabelama u bazi)
    // ====================================================================

    // Maper za osnovni model iz tabele teammate_ads
    public static class RawAdRowMapper implements RowMapper<com.example.backend.models.TeammateAd> {
        @Override
        public com.example.backend.models.TeammateAd mapRow(ResultSet rs, int rowNum) throws SQLException {
            com.example.backend.models.TeammateAd ad = new com.example.backend.models.TeammateAd();
            ad.setId(rs.getLong("id"));
            ad.setCreator_id(rs.getLong("creator_id"));
            ad.setSport(rs.getString("sport"));
            ad.setCity(rs.getString("city"));
            ad.setEvent_date(rs.getString("event_date"));
            ad.setEvent_time(rs.getString("event_time"));
            ad.setPlayers_needed(rs.getInt("players_needed"));
            ad.setStatus(rs.getString("status"));
            return ad;
        }
    }

    // Maper za osnovni model iz tabele teammate_ad_responses
    public static class RawResponseRowMapper implements RowMapper<com.example.backend.models.TeammateAdResponse> {
        @Override
        public com.example.backend.models.TeammateAdResponse mapRow(ResultSet rs, int rowNum) throws SQLException {
            com.example.backend.models.TeammateAdResponse response = new com.example.backend.models.TeammateAdResponse();
            response.setId(rs.getLong("id"));
            response.setAd_id(rs.getLong("ad_id"));
            response.setUser_id(rs.getLong("user_id"));
            response.setStatus(rs.getString("status"));
            return response;
        }
    }


    // ====================================================================
    // 2. MAPERI ZA DTO-ove (Za kompleksne prikaze i JOIN upite)
    // ====================================================================

    // Maper za javni oglas na oglasnoj tabli (vuče i username kreatora)
    public static class AdResponseRowMapper implements RowMapper<AdResponse> {
        @Override
        public AdResponse mapRow(ResultSet rs, int rowNum) throws SQLException {
            AdResponse ad = new AdResponse();
            ad.setId(rs.getLong("id"));
            ad.setCreator_username(rs.getString("creator_username"));
            ad.setSport(rs.getString("sport"));
            ad.setCity(rs.getString("city"));
            ad.setEvent_date(rs.getString("event_date"));
            ad.setEvent_time(rs.getString("event_time"));
            ad.setPlayers_needed(rs.getInt("players_needed"));
            ad.setStatus(rs.getString("status"));
            return ad;
        }
    }

    // Maper za listu prijava sa detaljima korisnika (ime, prezime, telefon, username)
    public static class TeammateResponseRowMapper implements RowMapper<TeammateResponseDTO> {
        @Override
        public TeammateResponseDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
            TeammateResponseDTO res = new TeammateResponseDTO();
            res.setResponse_id(rs.getLong("response_id"));
            res.setUser_id(rs.getLong("user_id"));
            res.setUsername(rs.getString("username"));
            res.setFirst_name(rs.getString("first_name"));
            res.setLast_name(rs.getString("last_name"));
            res.setPhone(rs.getString("phone"));
            res.setStatus(rs.getString("status"));
            return res;
        }
    }
}