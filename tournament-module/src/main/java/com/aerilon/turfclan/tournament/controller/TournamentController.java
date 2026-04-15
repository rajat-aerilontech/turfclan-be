package com.aerilon.turfclan.tournament.controller;

import com.aerilon.turfclan.pagination.CursorPageResponse;
import com.aerilon.turfclan.tournament.dto.TournamentCreateRequestDTO;
import com.aerilon.turfclan.tournament.dto.TournamentSummaryDTO;
import com.aerilon.turfclan.tournament.service.TournamentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tournaments")
@RequiredArgsConstructor
@Tag(name = "Tournaments", description = "Tournament APIs")
public class TournamentController {

    private final TournamentService tournamentService;

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_TM_USER')")
    @Operation(
            summary = "Get Tournaments",
            description = "Returns tournaments whose sport category matches the selected-sport-experience header using cursor-based pagination. Requires turf-mobile source-app."
    )
    public ResponseEntity<CursorPageResponse<TournamentSummaryDTO>> getTournaments(
            @RequestHeader("selected-sport-experience") String selectedSportExperience,
            @RequestParam(required = false) String cursor,
            @RequestParam(required = false) Integer limit
    ) {
        return ResponseEntity.ok(tournamentService.getTournaments(selectedSportExperience, cursor, limit));
    }

    @GetMapping("/my")
    @PreAuthorize("hasAuthority('ROLE_TM_USER')")
    @Operation(
            summary = "Get My Tournaments",
            description = "Returns tournaments created by the authenticated mobile user for the selected-sport-experience header. Requires turf-mobile source-app."
    )
    public ResponseEntity<List<TournamentSummaryDTO>> getMyTournaments(
            Authentication authentication,
            @RequestHeader("selected-sport-experience") String selectedSportExperience
    ) {
        return ResponseEntity.ok(
                tournamentService.getMyTournaments(authentication.getName(), selectedSportExperience)
        );
    }

    @GetMapping("/{tournamentId}")
    @PreAuthorize("hasAuthority('ROLE_TM_USER')")
    @Operation(
            summary = "Get Tournament Detail",
            description = "Returns a single tournament whose sport category matches the selected-sport-experience header. Requires turf-mobile source-app."
    )
    public ResponseEntity<TournamentSummaryDTO> getTournamentDetail(
            @RequestHeader("selected-sport-experience") String selectedSportExperience,
            @PathVariable String tournamentId
    ) {
        return ResponseEntity.ok(tournamentService.getTournamentDetail(selectedSportExperience, tournamentId));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_TM_USER')")
    @Operation(
            summary = "Create Tournament",
            description = "Creates a tournament for the authenticated mobile user and binds the admin to that user. Uses the selected-sport-experience header as sport category."
    )
    public ResponseEntity<TournamentSummaryDTO> createTournament(
            Authentication authentication,
            @RequestHeader("selected-sport-experience") String selectedSportExperience,
            @Valid @RequestBody TournamentCreateRequestDTO request
    ) {
        return ResponseEntity.ok(
                tournamentService.createTournament(authentication.getName(), selectedSportExperience, request)
        );
    }
}