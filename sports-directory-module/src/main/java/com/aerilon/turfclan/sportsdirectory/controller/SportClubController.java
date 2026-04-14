package com.aerilon.turfclan.sportsdirectory.controller;

import com.aerilon.turfclan.sportsdirectory.dto.SportClubDetailDTO;
import com.aerilon.turfclan.sportsdirectory.dto.SportClubSummaryDTO;
import com.aerilon.turfclan.sportsdirectory.dto.SportClubUpsertRequestDTO;
import com.aerilon.turfclan.sportsdirectory.service.SportClubService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/sports")
@RequiredArgsConstructor
@Tag(name = "Sports Directory", description = "Sports clubs directory APIs")
public class SportClubController {

    private final SportClubService sportClubService;

    @GetMapping("/clubs")
    @PreAuthorize("hasAnyAuthority('ROLE_TM_USER', 'ROLE_TA_USER')")
    @Operation(
            summary = "Get Sports Clubs",
            description = "Returns the list of sports clubs. Requires turf-mobile source-app."
    )
    public ResponseEntity<List<SportClubSummaryDTO>> getClubs() {
        return ResponseEntity.ok(sportClubService.getClubs());
    }

    @GetMapping("/clubs/{clubId}")
    @PreAuthorize("hasAnyAuthority('ROLE_TM_USER', 'ROLE_TA_USER')")
    @Operation(
            summary = "Get Sports Club Detail",
                        description = "Returns full detail for a sports club. Requires turf-mobile source-app."
    )
    public ResponseEntity<SportClubDetailDTO> getClubDetail(
            @PathVariable String clubId
    ) {
                return ResponseEntity.ok(sportClubService.getClubDetail(clubId));
    }

    @PostMapping("/clubs")
    @PreAuthorize("hasAuthority('ROLE_TA_USER')")
    @Operation(
            summary = "Create Sports Club",
            description = "Creates a sports club for admin web app requests. Requires turf-admin source-app. Returns 401 Unauthorized if source-app does not match expected role."
    )
    public ResponseEntity<SportClubDetailDTO> createClub(
            @Valid @RequestBody SportClubUpsertRequestDTO request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(sportClubService.createClub(request));
    }

    @PutMapping("/clubs/{clubId}")
    @PreAuthorize("hasAuthority('ROLE_TA_USER')")
    @Operation(
            summary = "Update Sports Club",
            description = "Updates a sports club for admin web app requests. Requires turf-admin source-app. Returns 401 Unauthorized if source-app does not match expected role."
    )
    public ResponseEntity<SportClubDetailDTO> updateClub(
            @PathVariable String clubId,
            @Valid @RequestBody SportClubUpsertRequestDTO request
    ) {
        return ResponseEntity.ok(
                sportClubService.updateClub(clubId, request)
        );
    }

    @DeleteMapping("/clubs/{clubId}")
    @PreAuthorize("hasAuthority('ROLE_TA_USER')")
    @Operation(
            summary = "Delete Sports Club",
            description = "Deletes a sports club for admin web app requests. Requires turf-admin source-app. Returns 401 Unauthorized if source-app does not match expected role."
    )
    public ResponseEntity<Void> deleteClub(
            @PathVariable String clubId
    ) {
        sportClubService.deleteClub(clubId);
        return ResponseEntity.noContent().build();
    }
}