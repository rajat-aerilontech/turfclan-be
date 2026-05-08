package com.aerilon.turfclan.partner.controller;

import com.aerilon.turfclan.facility.dto.FacilitiesRequestDto;
import com.aerilon.turfclan.partner.dto.*;
import com.aerilon.turfclan.partner.enums.OnboardStep;
import com.aerilon.turfclan.partner.service.OnboardingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/partner-onboarding")
@RequiredArgsConstructor
@Tag(name = "Partner Onboarding", description = "Partner Onboarding APIs")
public class OnboardingController {

    @Autowired
    private final OnboardingService onboardingService;

    /**
     * Returns all onboarding data for the authenticated partner.
     *
     * @param authentication authenticated principal containing the user id
     * @return full onboarding data
     */
    @GetMapping("/all-data")
    @PreAuthorize("hasAuthority('ROLE_TM_PARTNER')")
    @Operation(summary = "Get All Onboarding Data", description = "Returns all saved data for all steps to populate the UI.")
    public ResponseEntity<OnboardingFullDataDto> getAllOnboardingData(Authentication authentication) {
        String userId = authentication.getName();
        return ResponseEntity.ok(onboardingService.getFullOnboardingData(userId));
    }

    /**
     * Returns the current onboarding step for the authenticated partner.
     *
     * @param authentication authenticated principal containing the user id
     * @return current onboarding step
     */
    @GetMapping("/current-step")
    @PreAuthorize("hasAuthority('ROLE_TM_PARTNER')")
    @Operation(summary = "Get Current Onboarding Step", description = "Returns the current onboarding step for the authenticated partner.")
    public ResponseEntity<OnboardStep> getCurrentStep(Authentication authentication) {
        String userId = authentication.getName();
        return ResponseEntity.ok(onboardingService.getCurrentOnboardingStep(userId));
    }

    /**
     * Saves business info (step 1) for the authenticated partner.
     *
     * @param authentication authenticated principal containing the user id
     * @param dto business info payload
     * @return status message
     */
    @PostMapping("/step/business-info")
    @PreAuthorize("hasAuthority('ROLE_TM_PARTNER')")
    @Operation(summary = "Save Business Info", description = "Saves business information (Step 1) for the partner.")
    public ResponseEntity<String> saveBusinessInfo(
            Authentication authentication,
            @Valid @RequestBody BusinessInfoDto dto) {
        String userId = authentication.getName();
        onboardingService.saveBusinessInfo(userId, dto);
        return ResponseEntity.ok("Business info saved successfully. Proceed to Sports Details.");
    }

    /**
     * Saves facilities info (step 2) for the authenticated partner.
     *
     * @param authentication authenticated principal containing the user id
     * @param dto facilities payload
     * @return status message
     */
    @PostMapping("/step/sports-info")
    @PreAuthorize("hasAuthority('ROLE_TM_PARTNER')")
    @Operation(summary = "Save Facilities Info", description = "Saves sports and facilities information (Step 2) for the partner.")
    public ResponseEntity<String> saveFacilitiesInfo(
            Authentication authentication,
            @Valid @ModelAttribute FacilitiesRequestDto dto) {
        String userId = authentication.getName();
        onboardingService.saveFacilityInfo(userId, dto);
        return ResponseEntity.ok("Facilites info saved successfully. Proceed to Partner Details.");
    }

    /**
     * Saves partner details (step 3) for the authenticated partner.
     *
     * @param authentication authenticated principal containing the user id
     * @param dto partner detail payload
     * @return status message
     */
    @PostMapping("/step/partner-details")
    @PreAuthorize("hasAuthority('ROLE_TM_PARTNER')")
    @Operation(summary = "Save Partner Details", description = "Saves partner details (Step 3) for the partner.")
    public ResponseEntity<String> savePartnerDetails(
            Authentication authentication,
            @Valid @RequestBody PartnerDetailRequestDto dto) {
        String userId = authentication.getName();
        onboardingService.savePartnerDetails(userId, dto);
        return ResponseEntity.ok("Partner details saved successfully. Proceed to Bank Details.");
    }

    /**
     * Saves bank details (step 4) for the authenticated partner.
     *
     * @param authentication authenticated principal containing the user id
     * @param dto bank detail payload
     * @return status message
     */
    @PostMapping("/step/bank-details")
    @PreAuthorize("hasAuthority('ROLE_TM_PARTNER')")
    @Operation(summary = "Save Bank Details", description = "Saves bank details (Step 4) for the partner.")
    public ResponseEntity<String> saveBankDetails(
            Authentication authentication,
            @Valid @RequestBody BankDetailRequestDto dto) {
        String userId = authentication.getName();
        onboardingService.saveBankDetails(userId, dto);
        return ResponseEntity.ok("Bank details saved successfully. Proceed to Contract Details.");
    }

    /**
     * Signs the contract (step 5) for the authenticated partner and captures IP address.
     *
     * @param authentication authenticated principal containing the user id
     * @param dto contract payload
     * @param request HTTP request used to capture client IP
     * @return status message
     */
    @PostMapping("/step/contract")
    @PreAuthorize("hasAuthority('ROLE_TM_PARTNER')")
    @Operation(summary = "Submit Contract", description = "Signs the contract (Step 5) for the partner and captures the user's IP address.")
    public ResponseEntity<String> submitContract(
            Authentication authentication,
            @Valid @RequestBody ContractRequestDto dto,
            HttpServletRequest request) {
        String userId = authentication.getName();
        onboardingService.signContract(userId, dto, request);
        return ResponseEntity.ok("Contract Signed successfully. Proceed to submit the application!");
    }

    /**
     * Finalizes and submits the onboarding application.
     *
     * @param authentication authenticated principal containing the user id
     * @return status message
     */
    @PostMapping("/submit-application")
    @PreAuthorize("hasAuthority('ROLE_TM_PARTNER')")
    @Operation(summary = "Submit Application", description = "Finalizes and submits the onboarding application.")
    public ResponseEntity<String> submitApplication(Authentication authentication) {
        String userId = authentication.getName();
        onboardingService.submitApplication(userId);
        return ResponseEntity.ok("Application submitted successfully!");
    }
}
