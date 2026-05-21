package com.aerilon.turfclan.sportsdirectory.resolver;

import com.aerilon.turfclan.exception.InvalidRequestException;
import com.aerilon.turfclan.exception.ResourceNotFoundException;
import com.aerilon.turfclan.sportsdirectory.dto.SportOrganizationUpsertRequestDTO;
import com.aerilon.turfclan.sportsdirectory.repository.SportOrganizationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OrganizationResolver {
    private final SportOrganizationRepository sportOrganizationRepository;
//
//    public String requireAssociationId(String associationId) {
//        if (associationId == null || associationId.isBlank()) {
//            throw new InvalidRequestException("associationId is required");
//        }
//        return associationId.trim();
//    }
//
//
//    public Optional<SportAssociationEntity> findByAssociationIdentifier(String associationId) {
//        String idValue = requireAssociationId(associationId);
//
//        Optional<SportAssociationEntity> byId = findByUuidIfValid(idValue);
//        if (byId.isPresent()) {
//            return byId;
//        }
//
//        Optional<SportAssociationEntity> byShortName = sportAssociationRepository.findFirstByShortNameIgnoreCase(idValue);
//        if (byShortName.isPresent()) {
//            return byShortName;
//        }
//
//        return sportAssociationRepository.findFirstByNameIgnoreCase(idValue);
//    }
//
//    public SportAssociationEntity requireByAssociationIdentifier(String associationId) {
//        String idValue = requireAssociationId(associationId);
//        return findByAssociationIdentifier(idValue)
//                .orElseThrow(() -> new ResourceNotFoundException("Sports association not found for associationId: " + idValue));
//    }
//
//    public SportAssociationEntity requireByAssociationIdentifierGlobal(String associationId) {
//        String idValue = requireAssociationId(associationId);
//
//        Optional<SportAssociationEntity> byId;
//        try {
//            UUID parsedId = UUID.fromString(idValue);
//            byId = sportAssociationRepository.findById(parsedId);
//        } catch (IllegalArgumentException ignored) {
//            byId = Optional.empty();
//        }
//
//        if (byId.isPresent()) {
//            return byId.get();
//        }
//
//        Optional<SportAssociationEntity> byShortName = sportAssociationRepository.findFirstByShortNameIgnoreCase(idValue);
//        if (byShortName.isPresent()) {
//            return byShortName.get();
//        }
//
//        return sportAssociationRepository
//                .findFirstByNameIgnoreCase(idValue)
//                .orElseThrow(() -> new ResourceNotFoundException("Sports association not found for associationId: " + idValue));
//    }
//
//    private Optional<SportAssociationEntity> findByUuidIfValid(String associationId) {
//        try {
//            UUID parsedId = UUID.fromString(associationId);
//            return sportAssociationRepository.findById(parsedId);
//        } catch (IllegalArgumentException ignored) {
//            return Optional.empty();
//        }
//    }
}
