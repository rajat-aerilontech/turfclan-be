package com.aerilon.turfclan.partner.controller;

import com.aerilon.turfclan.facility.dto.FacilitiesRequestDto;
import com.aerilon.turfclan.partner.dto.*;
import com.aerilon.turfclan.partner.enums.OnboardStep;
import com.aerilon.turfclan.partner.service.OnboardingService;
import com.aerilon.turfclan.security.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
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
    
    @Autowired
    private final SecurityUtils securityUtils;

    /**
     * Returns all onboarding data for the authenticated partner.
     *
     * @return full onboarding data
     */
    @GetMapping("/all-data")
    @PreAuthorize("hasAuthority('ROLE_TM_PARTNER')")
    @Operation(summary = "Get All Onboarding Data", description = "Returns all saved data for all steps to populate the UI.")
    public ResponseEntity<OnboardingFullDataDto> getAllOnboardingData() {
        String userId = securityUtils.getCurrentUserId();
        return ResponseEntity.ok(onboardingService.getFullOnboardingData(userId));
    }

    /**
     * Saves business info (step 1) for the authenticated partner.
     *
     * @param dto business info payload
     * @return status message
     */
    @PostMapping("/step/business-info")
    @PreAuthorize("hasAuthority('ROLE_TM_PARTNER')")
    @Operation(summary = "Save Business Info", description = "Saves business information (Step 1) for the partner.")
    public ResponseEntity<String> saveBusinessInfo(
            @Valid @RequestBody BusinessInfoDto dto) {
        String userId = securityUtils.getCurrentUserId();
        onboardingService.saveBusinessInfo(userId, dto);
        return ResponseEntity.ok("Business info saved successfully. Proceed to Sports Details.");
    }

    /**
     * Saves facilities info (step 2) for the authenticated partner.
     *
     * @param dto facilities payload
     * @return status message
     */
    @PostMapping(value = "/step/facility-info", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('ROLE_TM_PARTNER')")
    @Operation(summary = "Save Facilities Info", description = "Saves sports and facilities information (Step 2) for the partner.")
    public ResponseEntity<String> saveFacilitiesInfo(
            @Valid @ModelAttribute FacilitiesRequestDto dto) {
        String userId = securityUtils.getCurrentUserId();
        onboardingService.saveFacilityInfo(userId, dto);
        return ResponseEntity.ok("Facilites info saved successfully. Proceed to Partner Details.");
    }

    /**
     * Saves partner details (step 3) for the authenticated partner.
     *
     * @param dto partner detail payload
     * @return status message
     */
    @PostMapping("/step/partner-details")
    @PreAuthorize("hasAuthority('ROLE_TM_PARTNER')")
    @Operation(summary = "Save Partner Details", description = "Saves partner details (Step 3) for the partner.")
    public ResponseEntity<String> savePartnerDetails(
            @Valid @RequestBody PartnerDetailRequestDto dto) {
        String userId = securityUtils.getCurrentUserId();
        onboardingService.savePartnerDetails(userId, dto);
        return ResponseEntity.ok("Partner details saved successfully. Proceed to Bank Details.");
    }

    /**
     * Saves bank details (step 4) for the authenticated partner.
     *
     * @param dto bank detail payload
     * @return status message
     */
    @PostMapping("/step/bank-details")
    @PreAuthorize("hasAuthority('ROLE_TM_PARTNER')")
    @Operation(summary = "Save Bank Details", description = "Saves bank details (Step 4) for the partner.")
    public ResponseEntity<String> saveBankDetails(
            @Valid @RequestBody BankDetailRequestDto dto) {
        String userId = securityUtils.getCurrentUserId();
        onboardingService.saveBankDetails(userId, dto);
        return ResponseEntity.ok("Bank details saved successfully. Proceed to Contract Details.");
    }

    /**
     * Signs the contract (step 5) for the authenticated partner and captures IP address.
     *
     * @param dto contract payload
     * @param request HTTP request used to capture client IP
     * @return status message
     */
    @PostMapping("/step/contract")
    @PreAuthorize("hasAuthority('ROLE_TM_PARTNER')")
    @Operation(summary = "Submit Contract", description = "Signs the contract (Step 5) for the partner and captures the user's IP address.")
    public ResponseEntity<String> submitContract(
            @Valid @RequestBody ContractRequestDto dto,
            HttpServletRequest request) {
        String userId = securityUtils.getCurrentUserId();
        onboardingService.signContract(userId, dto, request);
        return ResponseEntity.ok("Contract Signed successfully. Proceed to submit the application!");
    }

    /**
     * Finalizes and submits the onboarding application.
     *
     * @return status message
     */
    @PostMapping("/submit-application")
    @PreAuthorize("hasAuthority('ROLE_TM_PARTNER')")
    @Operation(summary = "Submit Application", description = "Finalizes and submits the onboarding application.")
    public ResponseEntity<String> submitApplication() {
        String userId = securityUtils.getCurrentUserId();
        onboardingService.submitApplication(userId);
        return ResponseEntity.ok("Application submitted successfully!");
    }
}
