package com.aerilon.turfclan.sportsdirectory.service;

import com.aerilon.turfclan.sportsdirectory.dto.SportClubDetailDTO;
import com.aerilon.turfclan.sportsdirectory.dto.SportClubSummaryDTO;
import com.aerilon.turfclan.sportsdirectory.dto.SportClubUpsertRequestDTO;

import java.util.List;

public interface SportClubService {

    List<SportClubSummaryDTO> getClubs();

    SportClubDetailDTO getClubDetail(String clubId);

    SportClubDetailDTO createClub(SportClubUpsertRequestDTO request);

    SportClubDetailDTO updateClub(String clubId, SportClubUpsertRequestDTO request);

    void deleteClub(String clubId);
}