package com.aerilon.turfclan.sportsdirectory.controller;

import com.aerilon.turfclan.sportsdirectory.dto.SportAssociationDetailDTO;
import com.aerilon.turfclan.sportsdirectory.dto.SportAssociationSummaryDTO;
import com.aerilon.turfclan.sportsdirectory.dto.SportAssociationUpsertRequestDTO;
import com.aerilon.turfclan.sportsdirectory.service.SportAssociationService;
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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/sports")
@RequiredArgsConstructor
@Tag(name = "Sports Directory", description = "Sports associations directory APIs")
public class SportAssociationController {

    private final SportAssociationService sportAssociationService;

    @GetMapping("/associations")
    @PreAuthorize("hasAnyAuthority('ROLE_TM_USER', 'ROLE_TA_USER')")
    @Operation(
            summary = "Get Sports Associations",
                        description = "Returns the list of sports associations based on selected-sport-experience. Requires turf-mobile source-app."
    )
    public ResponseEntity<List<SportAssociationSummaryDTO>> getAssociations(
                        @RequestHeader("selected-sport-experience") String selectedSportExperience
    ) {
                return ResponseEntity.ok(sportAssociationService.getAssociations(selectedSportExperience));
    }

    @GetMapping("/associations/{associationId}")
    @PreAuthorize("hasAnyAuthority('ROLE_TM_USER', 'ROLE_TA_USER')")
    @Operation(
            summary = "Get Sports Association Detail",
                        description = "Returns full detail for a sports association based on selected-sport-experience. Requires turf-mobile source-app."
    )
    public ResponseEntity<SportAssociationDetailDTO> getAssociationDetail(
                        @RequestHeader("selected-sport-experience") String selectedSportExperience,
            @PathVariable String associationId
    ) {
                return ResponseEntity.ok(sportAssociationService.getAssociationDetail(selectedSportExperience, associationId));
    }

    @PostMapping("/associations")
    @PreAuthorize("hasAuthority('ROLE_TA_USER')")
    @Operation(
            summary = "Create Sports Association",
            description = "Creates a sports association for admin web app requests. Requires turf-admin source-app. Returns 401 Unauthorized if source-app does not match expected role."
    )
    public ResponseEntity<SportAssociationDetailDTO> createAssociation(
            @Valid @RequestBody SportAssociationUpsertRequestDTO request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(sportAssociationService.createAssociation(request));
    }

    @PutMapping("/associations/{associationId}")
    @PreAuthorize("hasAuthority('ROLE_TA_USER')")
    @Operation(
            summary = "Update Sports Association",
            description = "Updates a sports association for admin web app requests. Requires turf-admin source-app. Returns 401 Unauthorized if source-app does not match expected role."
    )
    public ResponseEntity<SportAssociationDetailDTO> updateAssociation(
            @PathVariable String associationId,
            @Valid @RequestBody SportAssociationUpsertRequestDTO request
    ) {
        return ResponseEntity.ok(
                sportAssociationService.updateAssociation(associationId, request)
        );
    }

    @DeleteMapping("/associations/{associationId}")
    @PreAuthorize("hasAuthority('ROLE_TA_USER')")
    @Operation(
            summary = "Delete Sports Association",
            description = "Deletes a sports association for admin web app requests. Requires turf-admin source-app. Returns 401 Unauthorized if source-app does not match expected role."
    )
    public ResponseEntity<Void> deleteAssociation(
            @PathVariable String associationId
    ) {
        sportAssociationService.deleteAssociation(associationId);
        return ResponseEntity.noContent().build();
    }
}
