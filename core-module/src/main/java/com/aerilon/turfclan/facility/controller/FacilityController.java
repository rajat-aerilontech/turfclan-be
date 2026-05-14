package com.aerilon.turfclan.facility.controller;

import com.aerilon.turfclan.facility.service.FacilityService;
import com.aerilon.turfclan.facility.dto.FacilitiesMobileResponseDto;
import com.aerilon.turfclan.facility.dto.FacilityUpdateDto;
import com.aerilon.turfclan.facility.dto.SubFacilityUpdateDto;
import com.aerilon.turfclan.facility.dto.FacilitiesRequestDto;
import com.aerilon.turfclan.facility.dto.FacilityRequestDto;
import com.aerilon.turfclan.facility.dto.SubFacilityRequestDto;
import com.aerilon.turfclan.security.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/facility")
@RequiredArgsConstructor
@Tag(name = "Facility", description = "Facility APIs")
public class FacilityController {

    @Autowired
    private FacilityService facilityService;
    
    @Autowired
    private SecurityUtils securityUtils;

    /**
     * Returns facility data for the authenticated partner.
     *
     * @param authentication authenticated principal containing the user id
     * @return facility data for the partner
     */
    @GetMapping("/user")
    @PreAuthorize("hasAuthority('ROLE_TM_PARTNER')")
    @Operation(summary = "Get Facility For User", description = "Returns all facility data associated with the authenticated user (partner).")
    public ResponseEntity<FacilitiesRequestDto> getFacilityByUser() {
        String userId = securityUtils.getCurrentUserId();
        return ResponseEntity.ok(facilityService.getFacilityForUser(userId));
    }

    /**
     * Returns all facilities accessible to the authenticated user or partner.
     *
     * @param authentication authenticated principal
     * @return facility data list for the authenticated user
     */
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_TM_PARTNER', 'ROLE_TM_USER')")
    @Operation(summary = "Get All Facility", description = "Returns all facility data for the authenticated user, including any associated details.")
    public ResponseEntity<FacilitiesRequestDto> getAllFacility(Authentication authentication) {
        return ResponseEntity.ok(facilityService.getAllFacility());
    }

    /**
     * Returns nearby facilities for mobile users, optionally sorted and limited by distance.
     *
     * @param latitude user's latitude
     * @param longitude user's longitude
     * @param sortBy sorting strategy: distance, price, or distance_price
     * @param maxDistanceKm maximum search radius in kilometers
     * @return nearby facilities with distance and pricing info
     */
    @GetMapping("/mobile/nearby")
    @PreAuthorize("hasAuthority('ROLE_TM_USER')")
    @Operation(summary = "Get Nearby Facilities for Mobile Users",
            description = "Returns all nearby facilities sorted by proximity and price. Mobile users can view facilities with their distances and lowest available prices.")
    public ResponseEntity<FacilitiesMobileResponseDto> getNearbyFacilitiesForMobile(
            @Parameter(name = "latitude", description = "User's current latitude", required = true, in = ParameterIn.QUERY)
            @RequestParam Double latitude,

            @Parameter(name = "longitude", description = "User's current longitude", required = true, in = ParameterIn.QUERY)
            @RequestParam Double longitude,

            @Parameter(name = "sortBy", description = "Sorting strategy: 'distance' (nearest first), 'price' (lowest price first), or 'distance_price' (distance first then price - default)",
                    in = ParameterIn.QUERY)
            @RequestParam(required = false, defaultValue = "distance_price") String sortBy,

            @Parameter(name = "maxDistanceKm", description = "Maximum search radius in kilometers (default: 50 km)",
                    in = ParameterIn.QUERY)
            @RequestParam(required = false, defaultValue = "50") Double maxDistanceKm) {

        if (latitude == null || longitude == null) {
            throw new IllegalArgumentException("Latitude and longitude are required parameters");
        }

        FacilitiesMobileResponseDto response = facilityService.getAllFacilitiesForMobile(
                latitude, longitude, sortBy, maxDistanceKm);

        return ResponseEntity.ok(response);
    }

    /**
     * Returns facility details (including sub-facilities) for mobile users.
     *
     * @param facilityId facility identifier
     * @param authentication authenticated principal containing the user id
     * @return facility details with sub-facilities
     */
    @GetMapping("/mobile/facility/{facilityId}")
    @PreAuthorize("hasAnyAuthority('ROLE_TM_USER')")
    @Operation(
        summary = "Get Facility Details for Users",
        description = "Returns facility details with sub-facilities for the requested facility."
    )
    public ResponseEntity<FacilityRequestDto> getFacilityDetail(
        @PathVariable UUID facilityId,
        Authentication authentication
    ){
        FacilityRequestDto response = facilityService.getFacilityDetail(facilityId);
        return ResponseEntity.ok(response);
    }


    /**
     * Updates facility details for the authenticated partner.
     *
     * @param facilityId facility identifier
     * @param updateDto update payload
     * @param authentication authenticated principal containing the user id
     * @return updated facility data
     */
    @PutMapping("/{facilityId}")
    @PreAuthorize("hasAuthority('ROLE_TM_PARTNER')")
    @Operation(summary = "Update Facility Details", description = "Update facility details by the partner who owns the facility.")
    public ResponseEntity<FacilityRequestDto> updateFacility(
            @PathVariable UUID facilityId,
            @ModelAttribute FacilityUpdateDto updateDto) {
        String userId = securityUtils.getCurrentUserId();
        FacilityRequestDto response = facilityService.updateFacility(userId, facilityId, updateDto);
        return ResponseEntity.ok(response);
    }

    /**
     * Updates sport details within a facility for the authenticated partner.
     *
     * @param facilityId facility identifier
     * @param sportId sport identifier
     * @param updateDto update payload
     * @param authentication authenticated principal containing the user id
     * @return updated facility data
     */
    @PutMapping("/{facilityId}/sport/{sportId}")
    @PreAuthorize("hasAuthority('ROLE_TM_PARTNER')")
    @Operation(summary = "Update Sport Detail", description = "Update sport detail by the partner who owns the facility.")
    public ResponseEntity<FacilityRequestDto> updateSubFacility(
            @PathVariable UUID facilityId,
            @PathVariable UUID sportId,
            @RequestBody SubFacilityUpdateDto updateDto) {
        String userId = securityUtils.getCurrentUserId();
        FacilityRequestDto response = facilityService.updateSubFacility(userId, facilityId, sportId, updateDto);
        return ResponseEntity.ok(response);
    }

    /**
     * Adds a sport to a facility for the authenticated partner.
     *
     * @param facilityId facility identifier
     * @param subFacilityRequestDto sport payload
     * @param authentication authenticated principal containing the user id
     * @return updated facility data
     */
    @PostMapping("/{facilityId}/sport")
    @PreAuthorize("hasAuthority('ROLE_TM_PARTNER')")
    @Operation(summary = "Update Sport Detail", description = "Update sport detail by the partner who owns the facility.")
    public ResponseEntity<FacilityRequestDto> createSubFacilityForFacility(
            @PathVariable UUID facilityId,
            @Valid @RequestBody SubFacilityRequestDto subFacilityRequestDto) {
        String userId = securityUtils.getCurrentUserId();
        FacilityRequestDto response = facilityService.addSubFacilityToFacility(userId, facilityId, subFacilityRequestDto);
        return ResponseEntity.ok(response);
    }

    /**
     * Creates a facility for the authenticated partner.
     *
     * @param facilityRequestDto facility payload
     * @param authentication authenticated principal containing the user id
     * @return created facility data
     */
    @PostMapping("/user")
    @PreAuthorize("hasAuthority('ROLE_TM_PARTNER')")
    @Operation(summary = "Update Facility Details", description = "Update facility details by the partner who owns the facility.")
    public ResponseEntity<FacilityRequestDto> createFacilityForUser(
            @Valid @RequestBody FacilityRequestDto facilityRequestDto) {
        String userId = securityUtils.getCurrentUserId();
        FacilityRequestDto response = facilityService.addFacilityForUser(userId, facilityRequestDto);
        return ResponseEntity.ok(response);
    }
}
