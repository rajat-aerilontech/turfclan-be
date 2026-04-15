package com.aerilon.turfclan.tournament.resolver;

import com.aerilon.turfclan.exception.InvalidRequestException;
import org.springframework.stereotype.Component;

@Component
public class TournamentResolver {

    public String requireSelectedSportExperience(String selectedSportExperience) {
        if (selectedSportExperience == null || selectedSportExperience.isBlank()) {
            throw new InvalidRequestException("selected-sport-experience header value is required");
        }
        return selectedSportExperience.trim();
    }

    public String requireTournamentId(String tournamentId) {
        if (tournamentId == null || tournamentId.isBlank()) {
            throw new InvalidRequestException("tournamentId is required");
        }
        return tournamentId.trim();
    }
}