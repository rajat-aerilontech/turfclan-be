package com.aerilon.turfclan.partner.controller;

import com.aerilon.turfclan.partner.dto.DashboardDto;
import com.aerilon.turfclan.partner.dto.OnboardingFullDataDto;
import com.aerilon.turfclan.partner.service.PartnerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/partner")
@RequiredArgsConstructor
@Tag(name = "Partner", description = "Partner APIs")
public class PartnerController {

    @Autowired
    private final PartnerService partnerService;

    @GetMapping("/dashboard")
    @PreAuthorize("hasAuthority('ROLE_TM_PARTNER')")
    @Operation(summary = "Get Dashboard Data", description = "Returns all necessary data for the partner dashboard, including any relevant metrics.")
    public ResponseEntity<DashboardDto> getDashboard(Authentication authentication) {
        String userId = authentication.getName();
        return ResponseEntity.ok(partnerService.getFullDashboardData(userId));
    }
}
