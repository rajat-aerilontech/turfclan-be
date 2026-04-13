package com.aerilon.turfclan.user.controller;

import com.aerilon.turfclan.user.dto.AuthResponseDTO;
import com.aerilon.turfclan.user.dto.SendOtpRequestDTO;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

    @GetMapping("/{emailId}")
    @Operation(summary = "Get User by Email", description = "Fetches a user profile by their email address.")
    public ResponseEntity<UserDTO> getUserById(@PathVariable("emailId") String emailId) {
        return ResponseEntity.ok(userService.getUserByEmail(emailId));
    }

    @PostMapping("/otp/request")
    @Operation(summary = "Request OTP", description = "Generates and sends an OTP to a given phone number and confirms that the OTP was sent successfully.")
    public ResponseEntity<OtpResponseDTO> requestOtp(@RequestBody SendOtpRequestDTO request) {
        return ResponseEntity.ok(otpService.requestOtp(request));
    }

    @PostMapping("/otp/verify")
    @Operation(summary = "Verify OTP", description = "Verifies a submitted OTP and returns the user profile, tokens, and whether the user is new or existing.")
    public ResponseEntity<AuthResponseDTO> verifyOtp(@RequestBody OtpVerifyRequestDTO request) {
        return ResponseEntity.ok(otpService.verifyOtp(request));
    }

    @PostMapping("/signup")
    @Operation(
            summary = "Complete User Profile (Signup)",
            description = "Completes the user profile after OTP verification. Requires Bearer token."
    )
    public ResponseEntity<UserDTO> signup(
            Authentication authentication,
            @RequestBody @Valid SignupRequestDTO request
    ) {
        String userId = authentication.getName();
        return ResponseEntity.ok(userService.signup(userId, request));
    }
}
