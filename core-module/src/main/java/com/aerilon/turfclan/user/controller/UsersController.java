package com.aerilon.turfclan.user.controller;

import com.aerilon.turfclan.user.dto.AuthResponseDTO;
import com.aerilon.turfclan.user.dto.DashboardResponseDTO;
import com.aerilon.turfclan.user.dto.SendOtpRequestDTO;
import com.aerilon.turfclan.user.dto.OtpResponseDTO;
import com.aerilon.turfclan.user.dto.OtpVerifyRequestDTO;
import com.aerilon.turfclan.user.dto.SignupRequestDTO;
import com.aerilon.turfclan.user.dto.UserDTO;
import com.aerilon.turfclan.user.service.OtpService;
import com.aerilon.turfclan.user.service.UserService;
import com.aerilon.turfclan.security.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "User profile and OTP management APIs")
public class UsersController {

    private final UserService userService;

    @Autowired
    private final OtpService otpService;
    
    @Autowired
    private final SecurityUtils securityUtils;

    /**
     * Fetches a user profile by email address.
     *
     * @param emailId user email address
     * @return user profile
     */
    @GetMapping("/{emailId}")
    @PreAuthorize("hasAuthority('ROLE_TM_USER')")
    @Operation(summary = "Get User by Email", description = "Fetches a user profile by their email address.")
    public ResponseEntity<UserDTO> getUserById(@PathVariable("emailId") String emailId) {
        return ResponseEntity.ok(userService.getUserByEmail(emailId));
    }

    /**
     * Generates and sends an OTP to a phone number.
     *
     * @param sourceApp source application header
     * @param request OTP request payload
     * @return OTP request result
     */
    @PostMapping("/otp/request")
    @PreAuthorize("hasAnyAuthority('ROLE_TM_USER', 'ROLE_TM_PARTNER', 'ROLE_TA_USER')")
    @Operation(summary = "Request OTP", description = "Generates and sends an OTP to a given phone number and confirms that the OTP was sent successfully.")
    public ResponseEntity<OtpResponseDTO> requestOtp(
            @RequestHeader(value = "source-app", required = false, defaultValue = "user") String sourceApp,
            @RequestBody @Valid SendOtpRequestDTO request) {
        return ResponseEntity.ok(otpService.requestOtp(request, sourceApp));
    }

    /**
     * Verifies the submitted OTP and returns auth data.
     *
     * @param sourceApp source application header
     * @param request OTP verification payload
     * @return auth response and user profile
     */
    @PostMapping("/otp/verify")
    @PreAuthorize("hasAnyAuthority('ROLE_TM_USER', 'ROLE_TM_PARTNER', 'ROLE_TA_USER')")
    @Operation(summary = "Verify OTP", description = "Verifies a submitted OTP and returns the user profile, tokens, and whether the user is new or existing.")
    public ResponseEntity<AuthResponseDTO> verifyOtp(
            @RequestHeader(value = "source-app", required = false, defaultValue = "user") String sourceApp,
            @RequestBody @Valid OtpVerifyRequestDTO request,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse) {
        return ResponseEntity.ok(otpService.verifyOtp(request, sourceApp, httpRequest, httpResponse));
    }

    /**
     * Completes the user profile after OTP verification.
     *
     * @param request signup payload
     * @return updated user profile
     */
    @PostMapping( value = "/signup", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('ROLE_TM_USER')")
    @Operation(
            summary = "Complete User Profile (Signup)",
            description = "Completes the user profile after OTP verification. Requires Bearer token."
    )
    public ResponseEntity<UserDTO> signup(
            @ModelAttribute @Valid SignupRequestDTO request
    ) {
        String userId = securityUtils.getCurrentUserId();
        return ResponseEntity.ok(userService.signup(userId, request));
    }

    /**
     * Returns dashboard details for the authenticated user.
     *
     * @param selectedSportExperience selected sport experience header
     * @return dashboard details
     */
    @GetMapping("/dashboard")
    @PreAuthorize("hasAuthority('ROLE_TM_USER')")
    @Operation(
            summary = "Get Dashboard Details",
            description = "Returns dashboard details scoped by the selected-sport-experience header for the authenticated user."
    )
    public ResponseEntity<DashboardResponseDTO> getDashboard(
            @RequestHeader("selected-sport-experience") String selectedSportExperience
    ) {
        String userId = securityUtils.getCurrentUserId();
        return ResponseEntity.ok(userService.getDashboard(userId, selectedSportExperience));
    }
}
