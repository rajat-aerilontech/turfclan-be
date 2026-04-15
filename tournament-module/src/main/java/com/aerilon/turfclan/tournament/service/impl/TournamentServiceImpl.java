package com.aerilon.turfclan.tournament.service.impl;

import com.aerilon.turfclan.exception.InvalidRequestException;
import com.aerilon.turfclan.pagination.CursorPageResponse;
import com.aerilon.turfclan.pagination.CursorPageToken;
import com.aerilon.turfclan.pagination.CursorPaginationUtils;
import com.aerilon.turfclan.exception.ResourceNotFoundException;
import com.aerilon.turfclan.tournament.converter.TournamentConverter;
import com.aerilon.turfclan.tournament.dto.TournamentCreateRequestDTO;
import com.aerilon.turfclan.tournament.dto.TournamentSummaryDTO;
import com.aerilon.turfclan.tournament.entity.TournamentEntity;
import com.aerilon.turfclan.tournament.repository.TournamentRepository;
import com.aerilon.turfclan.tournament.resolver.TournamentResolver;
import com.aerilon.turfclan.tournament.service.TournamentService;
import com.aerilon.turfclan.user.entity.UserEntity;
import com.aerilon.turfclan.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TournamentServiceImpl implements TournamentService {

    private final TournamentRepository tournamentRepository;
    private final TournamentConverter tournamentConverter;
    private final TournamentResolver tournamentResolver;
    private final UserRepository userRepository;

    @Override
    public CursorPageResponse<TournamentSummaryDTO> getTournaments(String selectedSportExperience,
                                                                   String cursor,
                                                                   Integer limit) {
        String sportCategory = tournamentResolver.requireSelectedSportExperience(selectedSportExperience);
        int normalizedLimit = CursorPaginationUtils.normalizeLimit(limit);
        CursorPageToken cursorToken = CursorPaginationUtils.decodeCursor(cursor);
        PageRequest pageRequest = PageRequest.of(0, normalizedLimit + 1);

        List<TournamentEntity> entities;
        if (cursorToken == null) {
            entities = tournamentRepository.findBySportCategoryIgnoreCaseOrderByCreatedAtDescIdDesc(
                    sportCategory,
                    pageRequest
            );
        } else {
            entities = tournamentRepository.findPageBySportCategoryIgnoreCaseAfterCursor(
                    sportCategory,
                    cursorToken.createdAt(),
                    cursorToken.id(),
                    pageRequest
            );
        }

        if (entities.isEmpty()) {
            throw new ResourceNotFoundException("No tournaments found for selected-sport-experience: " + sportCategory);
        }

        return CursorPaginationUtils.buildResponse(
                entities,
                normalizedLimit,
                tournamentConverter::toSummary,
                TournamentEntity::getCreatedAt,
                TournamentEntity::getId
        );
    }

    @Override
    public List<TournamentSummaryDTO> getMyTournaments(String userId, String selectedSportExperience) {
        String sportCategory = tournamentResolver.requireSelectedSportExperience(selectedSportExperience);
        UserEntity admin = resolveAdmin(userId);

        List<TournamentEntity> entities = tournamentRepository
                .findByAdminIdAndSportCategoryIgnoreCase(admin.getId(), sportCategory);

        if (entities.isEmpty()) {
            throw new ResourceNotFoundException(
                    "No tournaments found for user id: " + userId + " and selected-sport-experience: " + sportCategory
            );
        }

        List<TournamentSummaryDTO> summaries = new ArrayList<>();
        for (TournamentEntity entity : entities) {
            summaries.add(tournamentConverter.toSummary(entity));
        }
        return summaries;
    }

    @Override
    public TournamentSummaryDTO getTournamentDetail(String selectedSportExperience, String tournamentId) {
        String sportCategory = tournamentResolver.requireSelectedSportExperience(selectedSportExperience);
        String idValue = tournamentResolver.requireTournamentId(tournamentId);

        TournamentEntity entity = findTournamentByIdAndSportCategory(idValue, sportCategory)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Tournament not found for tournamentId: " + idValue + " and selected-sport-experience: " + sportCategory
                ));

        return tournamentConverter.toSummary(entity);
    }

    @Override
    @Transactional
    public TournamentSummaryDTO createTournament(String userId,
                                                 String selectedSportExperience,
                                                 TournamentCreateRequestDTO request) {
        String sportCategory = tournamentResolver.requireSelectedSportExperience(selectedSportExperience);
        UserEntity admin = resolveAdmin(userId);

        TournamentEntity entity = new TournamentEntity();
        tournamentConverter.applyCreateRequest(entity, request, sportCategory);
        entity.setAdmin(admin);

        TournamentEntity saved = tournamentRepository.save(entity);
        return tournamentConverter.toSummary(saved);
    }

    private UserEntity resolveAdmin(String userId) {
        try {
            UUID adminId = UUID.fromString(userId);
            return userRepository.findById(adminId)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found for id: " + userId));
        } catch (IllegalArgumentException ex) {
            throw new InvalidRequestException("Invalid authenticated user id");
        }
    }

    private Optional<TournamentEntity> findTournamentByIdAndSportCategory(String tournamentId, String sportCategory) {
        try {
            UUID parsedId = UUID.fromString(tournamentId);
            return tournamentRepository.findByIdAndSportCategoryIgnoreCase(parsedId, sportCategory);
        } catch (IllegalArgumentException ex) {
            throw new InvalidRequestException("Invalid tournamentId");
        }
    }
}