package com.aerilon.turfclan.sportsdirectory.service.impl;

import com.aerilon.turfclan.exception.ResourceNotFoundException;
import com.aerilon.turfclan.sportsdirectory.converter.SportClubConverter;
import com.aerilon.turfclan.sportsdirectory.dto.SportClubDetailDTO;
import com.aerilon.turfclan.sportsdirectory.dto.SportClubSummaryDTO;
import com.aerilon.turfclan.sportsdirectory.dto.SportClubUpsertRequestDTO;
import com.aerilon.turfclan.sportsdirectory.entity.SportClubEntity;
import com.aerilon.turfclan.sportsdirectory.repository.SportClubRepository;
import com.aerilon.turfclan.sportsdirectory.resolver.SportClubResolver;
import com.aerilon.turfclan.sportsdirectory.service.SportClubService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SportClubServiceImpl implements SportClubService {

    private final SportClubRepository sportClubRepository;
    private final SportClubConverter sportClubConverter;
    private final SportClubResolver sportClubResolver;

    @Override
    public List<SportClubSummaryDTO> getClubs() {
        List<SportClubEntity> entities = sportClubRepository.findAll();

        if (entities.isEmpty()) {
            throw new ResourceNotFoundException("No sports clubs found");
        }

        List<SportClubSummaryDTO> summaries = new ArrayList<>();
        for (SportClubEntity entity : entities) {
            summaries.add(sportClubConverter.toSummary(entity));
        }

        return summaries;
    }

    @Override
    public SportClubDetailDTO getClubDetail(String clubId) {
        SportClubEntity entity = sportClubResolver.requireByClubIdentifier(clubId);
        return sportClubConverter.toDetail(entity);
    }

    @Override
    @Transactional
    public SportClubDetailDTO createClub(SportClubUpsertRequestDTO request) {
        sportClubResolver.validateUpsertRequest(request);

        SportClubEntity entity = new SportClubEntity();
        sportClubConverter.applyUpsertRequest(entity, request);

        SportClubEntity saved = sportClubRepository.save(entity);
        return sportClubConverter.toDetail(saved);
    }

    @Override
    @Transactional
    public SportClubDetailDTO updateClub(String clubId, SportClubUpsertRequestDTO request) {
        sportClubResolver.validateUpsertRequest(request);
        SportClubEntity entity =
                sportClubResolver.requireByClubIdentifierGlobal(clubId);

        sportClubConverter.applyUpsertRequest(entity, request);

        SportClubEntity saved = sportClubRepository.save(entity);
        return sportClubConverter.toDetail(saved);
    }

    @Override
    @Transactional
    public void deleteClub(String clubId) {
        SportClubEntity entity =
                sportClubResolver.requireByClubIdentifierGlobal(clubId);
        sportClubRepository.delete(entity);
    }
}