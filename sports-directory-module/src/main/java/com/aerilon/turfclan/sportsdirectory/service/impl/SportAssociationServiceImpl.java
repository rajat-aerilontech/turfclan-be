package com.aerilon.turfclan.sportsdirectory.service.impl;

import com.aerilon.turfclan.exception.ResourceNotFoundException;
import com.aerilon.turfclan.sportsdirectory.converter.SportAssociationConverter;
import com.aerilon.turfclan.sportsdirectory.dto.SportAssociationDetailDTO;
import com.aerilon.turfclan.sportsdirectory.dto.SportAssociationSummaryDTO;
import com.aerilon.turfclan.sportsdirectory.dto.SportAssociationUpsertRequestDTO;
import com.aerilon.turfclan.sportsdirectory.entity.SportAssociationEntity;
import com.aerilon.turfclan.sportsdirectory.repository.SportAssociationRepository;
import com.aerilon.turfclan.sportsdirectory.resolver.SportAssociationResolver;
import com.aerilon.turfclan.sportsdirectory.service.SportAssociationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SportAssociationServiceImpl implements SportAssociationService {

    private final SportAssociationRepository sportAssociationRepository;
    private final SportAssociationConverter sportAssociationConverter;
    private final SportAssociationResolver sportAssociationResolver;

    @Override
    public List<SportAssociationSummaryDTO> getAssociations() {
        List<SportAssociationEntity> entities = sportAssociationRepository.findAll();

        if (entities.isEmpty()) {
            throw new ResourceNotFoundException("No sports associations found");
        }

        List<SportAssociationSummaryDTO> summaries = new ArrayList<>();
        for (SportAssociationEntity entity : entities) {
            summaries.add(sportAssociationConverter.toSummary(entity));
        }

        return summaries;
    }

    @Override
    public SportAssociationDetailDTO getAssociationDetail(String associationId) {
        SportAssociationEntity entity = sportAssociationResolver.requireByAssociationIdentifier(associationId);
        return sportAssociationConverter.toDetail(entity);
    }

    @Override
    @Transactional
    public SportAssociationDetailDTO createAssociation(SportAssociationUpsertRequestDTO request) {
        sportAssociationResolver.validateUpsertRequest(request);

        SportAssociationEntity entity = new SportAssociationEntity();
        sportAssociationConverter.applyUpsertRequest(entity, request);

        SportAssociationEntity saved = sportAssociationRepository.save(entity);
        return sportAssociationConverter.toDetail(saved);
    }

    @Override
    @Transactional
    public SportAssociationDetailDTO updateAssociation(String associationId,
                                                       SportAssociationUpsertRequestDTO request) {
        sportAssociationResolver.validateUpsertRequest(request);
        SportAssociationEntity entity =
                sportAssociationResolver.requireByAssociationIdentifierGlobal(associationId);

        sportAssociationConverter.applyUpsertRequest(entity, request);

        SportAssociationEntity saved = sportAssociationRepository.save(entity);
        return sportAssociationConverter.toDetail(saved);
    }

    @Override
    @Transactional
    public void deleteAssociation(String associationId) {
        SportAssociationEntity entity =
                sportAssociationResolver.requireByAssociationIdentifierGlobal(associationId);
        sportAssociationRepository.delete(entity);
    }
}
