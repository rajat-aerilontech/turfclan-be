package com.aerilon.turfclan.user.controller;

import com.aerilon.turfclan.jwt.JwtService;
import com.aerilon.turfclan.user.dto.AuthResponseDTO;
import com.aerilon.turfclan.user.dto.OtpRequestDTO;
import com.aerilon.turfclan.user.dto.OtpResponseDTO;
import com.aerilon.turfclan.user.dto.OtpVerifyRequestDTO;
import com.aerilon.turfclan.user.dto.SignupRequestDTO;
import com.aerilon.turfclan.user.dto.UserDTO;
import com.aerilon.turfclan.user.service.OtpService;
import com.aerilon.turfclan.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "User profile and OTP management APIs")
public class UsersController {

    private final UserService userService;
    private final OtpService otpService;
    private final JwtService jwtService;

    @GetMapping("/{emailId}")
    @Operation(summary = "Get User by Email", description = "Fetches a user profile by their email address.")
    public ResponseEntity<UserDTO> getUserById(@PathVariable("emailId") String emailId) {
        return ResponseEntity.ok(userService.getUserByEmail(emailId));
    }

    @PostMapping("/otp/request")
    @Operation(summary = "Request OTP", description = "Generates and sends an OTP to a given phone number.")
    public ResponseEntity<OtpResponseDTO> requestOtp(@RequestBody OtpRequestDTO request) {
        return ResponseEntity.ok(otpService.requestOtp(request));
    }

    @PostMapping("/otp/verify")
    @Operation(summary = "Verify OTP", description = "Verifies a submitted OTP and returns the user profile along with access & refresh tokens.")
    public ResponseEntity<AuthResponseDTO> verifyOtp(@RequestBody OtpVerifyRequestDTO request) {
        return ResponseEntity.ok(otpService.verifyOtp(request));
    }

    /**
     * POST /api/v1/users/signup
     *
     * Completes the user profile after OTP authentication.
     * Requires a valid Bearer access token in the Authorization header.
     *
     * Content-Type: multipart/form-data
     *   - request   (application/json) : SignupRequestDTO
     *   - profilePic (image/*)          : optional profile picture file
     */
    @PostMapping(value = "/signup", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "Complete User Profile (Signup)",
            description = "Completes the user profile after OTP verification. Requires Bearer token. "
                    + "Send as multipart/form-data: 'request' part (JSON) + optional 'profilePic' part (image file)."
    )
    public ResponseEntity<UserDTO> signup(
            @RequestHeader("Authorization") String authHeader,
            @RequestPart("request") @Valid SignupRequestDTO request,
            @RequestPart(value = "profilePic", required = false) MultipartFile profilePic
    ) {
        String userId = jwtService.extractSubjectFromHeader(authHeader);
        return ResponseEntity.ok(userService.signup(userId, request, profilePic));
    }
}
