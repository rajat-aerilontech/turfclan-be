package com.aerilon.turfclan.sportsdirectory.resolver;

import com.aerilon.turfclan.exception.InvalidRequestException;
import com.aerilon.turfclan.exception.ResourceNotFoundException;
import com.aerilon.turfclan.sportsdirectory.dto.SportClubUpsertRequestDTO;
import com.aerilon.turfclan.sportsdirectory.entity.SportClubEntity;
import com.aerilon.turfclan.sportsdirectory.repository.SportClubRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SportClubResolver {

    private final SportClubRepository sportClubRepository;

    public String requireClubId(String clubId) {
        if (clubId == null || clubId.isBlank()) {
            throw new InvalidRequestException("clubId is required");
        }
        return clubId.trim();
    }

    public void validateUpsertRequest(SportClubUpsertRequestDTO request) {
        if (request == null) {
            throw new InvalidRequestException("Request body is required");
        }

        if (request.getName() == null || request.getName().isBlank()) {
            throw new InvalidRequestException("Club name is required");
        }
    }

    public Optional<SportClubEntity> findByClubIdentifier(String clubId) {
        String idValue = requireClubId(clubId);

        Optional<SportClubEntity> byId = findByUuidIfValid(idValue);
        if (byId.isPresent()) {
            return byId;
        }

        Optional<SportClubEntity> byShortName = sportClubRepository.findFirstByShortNameIgnoreCase(idValue);
        if (byShortName.isPresent()) {
            return byShortName;
        }

        return sportClubRepository.findFirstByNameIgnoreCase(idValue);
    }

    public SportClubEntity requireByClubIdentifier(String clubId) {
        String idValue = requireClubId(clubId);
        return findByClubIdentifier(idValue)
            .orElseThrow(() -> new ResourceNotFoundException("Sports club not found for clubId: " + idValue));
    }

    public SportClubEntity requireByClubIdentifierGlobal(String clubId) {
        String idValue = requireClubId(clubId);

        Optional<SportClubEntity> byId;
        try {
            UUID parsedId = UUID.fromString(idValue);
            byId = sportClubRepository.findById(parsedId);
        } catch (IllegalArgumentException ignored) {
            byId = Optional.empty();
        }

        if (byId.isPresent()) {
            return byId.get();
        }

        Optional<SportClubEntity> byShortName = sportClubRepository.findFirstByShortNameIgnoreCase(idValue);
        if (byShortName.isPresent()) {
            return byShortName.get();
        }

        return sportClubRepository
                .findFirstByNameIgnoreCase(idValue)
            .orElseThrow(() -> new ResourceNotFoundException("Sports club not found for clubId: " + idValue));
    }

    private Optional<SportClubEntity> findByUuidIfValid(String clubId) {
        try {
            UUID parsedId = UUID.fromString(clubId);
            return sportClubRepository.findById(parsedId);
        } catch (IllegalArgumentException ignored) {
            return Optional.empty();
        }
    }
}