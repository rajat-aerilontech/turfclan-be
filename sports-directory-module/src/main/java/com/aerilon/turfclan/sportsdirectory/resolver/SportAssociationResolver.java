package com.aerilon.turfclan.sportsdirectory.resolver;

import com.aerilon.turfclan.exception.InvalidRequestException;
import com.aerilon.turfclan.exception.UserNotFoundException;
import com.aerilon.turfclan.sportsdirectory.dto.SportAssociationUpsertRequestDTO;
import com.aerilon.turfclan.sportsdirectory.entity.SportAssociationEntity;
import com.aerilon.turfclan.sportsdirectory.repository.SportAssociationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SportAssociationResolver {

    private final SportAssociationRepository sportAssociationRepository;

    public String requireSelectedSportExperience(String selectedSportExperience) {
        if (selectedSportExperience == null || selectedSportExperience.isBlank()) {
            throw new InvalidRequestException("selected-sport-experience header value is required");
        }
        return selectedSportExperience.trim();
    }

    public String requireAssociationId(String associationId) {
        if (associationId == null || associationId.isBlank()) {
            throw new InvalidRequestException("associationId is required");
        }
        return associationId.trim();
    }

    public void validateUpsertRequest(SportAssociationUpsertRequestDTO request) {
        if (request == null) {
            throw new InvalidRequestException("Request body is required");
        }

        if (request.getName() == null || request.getName().isBlank()) {
            throw new InvalidRequestException("Association name is required");
        }

        if (request.getSelectedSportExperience() == null || request.getSelectedSportExperience().isBlank()) {
            throw new InvalidRequestException("selectedSportExperience is required in request body");
        }
    }

    public Optional<SportAssociationEntity> findByAssociationIdentifier(String selectedSportExperience, String associationId) {
        String sportExperience = requireSelectedSportExperience(selectedSportExperience);
        String idValue = requireAssociationId(associationId);

        Optional<SportAssociationEntity> byId = findByUuidIfValid(sportExperience, idValue);
        if (byId.isPresent()) {
            return byId;
        }

        Optional<SportAssociationEntity> byShortName =
                sportAssociationRepository.findFirstByShortNameIgnoreCaseAndSelectedSportExperienceIgnoreCase(idValue, sportExperience);
        if (byShortName.isPresent()) {
            return byShortName;
        }

        return sportAssociationRepository.findFirstByNameIgnoreCaseAndSelectedSportExperienceIgnoreCase(idValue, sportExperience);
    }

    public SportAssociationEntity requireByAssociationIdentifier(String selectedSportExperience, String associationId) {
        String idValue = requireAssociationId(associationId);
        return findByAssociationIdentifier(selectedSportExperience, idValue)
                .orElseThrow(() -> new UserNotFoundException("Sports association not found for associationId: " + idValue));
    }

    public SportAssociationEntity requireByAssociationIdentifierGlobal(String associationId) {
        String idValue = requireAssociationId(associationId);

        Optional<SportAssociationEntity> byId;
        try {
            UUID parsedId = UUID.fromString(idValue);
            byId = sportAssociationRepository.findById(parsedId);
        } catch (IllegalArgumentException ignored) {
            byId = Optional.empty();
        }

        if (byId.isPresent()) {
            return byId.get();
        }

        Optional<SportAssociationEntity> byShortName = sportAssociationRepository.findFirstByShortNameIgnoreCase(idValue);
        if (byShortName.isPresent()) {
            return byShortName.get();
        }

        return sportAssociationRepository
                .findFirstByNameIgnoreCase(idValue)
                .orElseThrow(() -> new UserNotFoundException("Sports association not found for associationId: " + idValue));
    }

    private Optional<SportAssociationEntity> findByUuidIfValid(String selectedSportExperience, String associationId) {
        try {
            UUID parsedId = UUID.fromString(associationId);
            return sportAssociationRepository.findByIdAndSelectedSportExperienceIgnoreCase(parsedId, selectedSportExperience);
        } catch (IllegalArgumentException ignored) {
            return Optional.empty();
        }
    }
}
