package com.aerilon.turfclan.sportsdirectory.controller;

import com.aerilon.turfclan.pagination.CursorPageResponse;
import com.aerilon.turfclan.sportsdirectory.dto.OrganizationSummaryDto;
import com.aerilon.turfclan.sportsdirectory.dto.SportOrganizationDetailDto;
import com.aerilon.turfclan.sportsdirectory.dto.SportOrganizationUpsertRequestDTO;
import com.aerilon.turfclan.sportsdirectory.enums.OrganizationType;
import com.aerilon.turfclan.sportsdirectory.service.SportOrganizationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/sports")
@RequiredArgsConstructor
@Tag(name = "Sports Directory", description = "Sports directory APIs")
public class SportOrganizationController {

    private final SportOrganizationService sportOrganizationService;

    /**
     * Returns the list of sports associations with cursor pagination.
     *
     * @param organizationType optional filter by organization type
     * @param cursor           pagination cursor for the next page
     * @param limit            maximum number of records to return
     * @return paginated list of sports associations
     */
    @GetMapping("/organizations")
    @PreAuthorize("hasAnyAuthority('ROLE_TM_USER', 'ROLE_TA_USER')")
    @Operation(
            summary = "Get Sports Organizations",
            description = "Returns the paginated list of sports organizations. Optionally filter by OrganizationType. Requires turf-mobile source-app."
    )
    public ResponseEntity<CursorPageResponse<OrganizationSummaryDto>> fetchOrganizationBySport(
            @RequestParam(value = "organizationType", required = false) OrganizationType organizationType,
            @RequestParam(required = false) String cursor,
            @RequestParam(required = false) Integer limit) {
        return ResponseEntity.ok(sportOrganizationService.getOrganizationBySport(organizationType, cursor, limit));
    }

//    /**
//     * Returns full details for a sports association.
//     *
//     * @param associationId association identifier
//     * @return sports association detail
//     */
//    @GetMapping("/associations/{associationId}")
//    @PreAuthorize("hasAnyAuthority('ROLE_TM_USER', 'ROLE_TA_USER')")
//    @Operation(
//            summary = "Get Sports Association Detail",
//            description = "Returns full detail for a sports association. Requires turf-mobile source-app."
//    )
//    public ResponseEntity<SportAssociationDetailDTO> getAssociationDetail(
//            @PathVariable String associationId
//    ) {
//        return ResponseEntity.ok(sportAssociationService.getAssociationDetail(associationId));
//    }
//
    /**
     * Creates a sports association.
     *
     * @param request create payload (JSON)
     */
    @PostMapping(value = "/organizations", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('ROLE_TA_USER')")
    @Operation(
            summary = "Create Sports Association",
            description = "Creates a sports association for admin web app requests. Requires turf-admin source-app. Returns 401 Unauthorized if source-app does not match expected role."
    )
    public ResponseEntity<Void> createAssociation(
            @Valid @ModelAttribute SportOrganizationUpsertRequestDTO request
    ) {
        sportOrganizationService.createAssociation(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
//
//    /**
//     * Updates a sports association.
//     *
//     * @param associationId association identifier
//     * @param request update payload
//     * @return updated sports association
//     */
//    @PutMapping("/associations/{associationId}")
//    @PreAuthorize("hasAuthority('ROLE_TA_USER')")
//    @Operation(
//            summary = "Update Sports Association",
//            description = "Updates a sports association for admin web app requests. Requires turf-admin source-app. Returns 401 Unauthorized if source-app does not match expected role."
//    )
//    public ResponseEntity<SportAssociationDetailDTO> updateAssociation(
//            @PathVariable String associationId,
//            @Valid @RequestBody SportAssociationUpsertRequestDTO request
//    ) {
//        return ResponseEntity.ok(
//                sportAssociationService.updateAssociation(associationId, request)
//        );
//    }
//
//    /**
//     * Deletes a sports association.
//     *
//     * @param associationId association identifier
//     * @return empty response on success
//     */
//    @DeleteMapping("/associations/{associationId}")
//    @PreAuthorize("hasAuthority('ROLE_TA_USER')")
//    @Operation(
//            summary = "Delete Sports Association",
//            description = "Deletes a sports association for admin web app requests. Requires turf-admin source-app. Returns 401 Unauthorized if source-app does not match expected role."
//    )
//    public ResponseEntity<Void> deleteAssociation(
//            @PathVariable String associationId
//    ) {
//        sportAssociationService.deleteAssociation(associationId);
//        return ResponseEntity.noContent().build();
//    }
}
