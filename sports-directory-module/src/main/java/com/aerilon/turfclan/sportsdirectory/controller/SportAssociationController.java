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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/sports")
@RequiredArgsConstructor
@Tag(name = "Sports Directory", description = "Sports associations directory APIs")
public class SportAssociationController {

    private final SportAssociationService sportAssociationService;

        /**
         * Returns the list of sports associations.
         *
         * @return list of sports associations
         */
        @GetMapping("/associations")
    @PreAuthorize("hasAnyAuthority('ROLE_TM_USER', 'ROLE_TA_USER')")
    @Operation(
            summary = "Get Sports Associations",
                        description = "Returns the list of sports associations. Requires turf-mobile source-app."
    )
        public ResponseEntity<List<SportAssociationSummaryDTO>> getAssociations() {
                return ResponseEntity.ok(sportAssociationService.getAssociations());
    }

        /**
         * Returns full details for a sports association.
         *
         * @param associationId association identifier
         * @return sports association detail
         */
        @GetMapping("/associations/{associationId}")
    @PreAuthorize("hasAnyAuthority('ROLE_TM_USER', 'ROLE_TA_USER')")
    @Operation(
            summary = "Get Sports Association Detail",
                        description = "Returns full detail for a sports association. Requires turf-mobile source-app."
    )
    public ResponseEntity<SportAssociationDetailDTO> getAssociationDetail(
            @PathVariable String associationId
    ) {
                return ResponseEntity.ok(sportAssociationService.getAssociationDetail(associationId));
    }

        /**
         * Creates a sports association.
         *
         * @param request create payload
         * @return created sports association
         */
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

        /**
         * Updates a sports association.
         *
         * @param associationId association identifier
         * @param request update payload
         * @return updated sports association
         */
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

        /**
         * Deletes a sports association.
         *
         * @param associationId association identifier
         * @return empty response on success
         */
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
