package com.aerilon.turfclan.tournament.service;

import com.aerilon.turfclan.pagination.CursorPageResponse;
import com.aerilon.turfclan.tournament.dto.TournamentCreateRequestDTO;
import com.aerilon.turfclan.tournament.dto.TournamentSummaryDTO;

import java.util.List;

public interface TournamentService {

    CursorPageResponse<TournamentSummaryDTO> getTournaments(String selectedSportExperience,
                                                            String cursor,
                                                            Integer limit);

    List<TournamentSummaryDTO> getMyTournaments(String userId, String selectedSportExperience);

    TournamentSummaryDTO getTournamentDetail(String selectedSportExperience, String tournamentId);

    TournamentSummaryDTO createTournament(String userId,
                                          String selectedSportExperience,
                                          TournamentCreateRequestDTO request);
}