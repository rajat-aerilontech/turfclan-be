package com.aerilon.turfclan.user.controller;

import com.aerilon.turfclan.user.dto.OtpRequestDTO;
import com.aerilon.turfclan.user.dto.OtpResponseDTO;
import com.aerilon.turfclan.user.dto.OtpVerifyRequestDTO;
import com.aerilon.turfclan.user.dto.UserDTO;
import com.aerilon.turfclan.user.service.OtpService;
import com.aerilon.turfclan.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UsersController {

    private final UserService userService;
    private final OtpService otpService;

    @GetMapping("/{emailId}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable("emailId") String emailId) {
        return ResponseEntity.ok(userService.getUserByEmail(emailId));
    }

    @PostMapping("/otp/request")
    public ResponseEntity<OtpResponseDTO> requestOtp(@RequestBody OtpRequestDTO request) {
        return ResponseEntity.ok(otpService.requestOtp(request));
    }

    @PostMapping("/otp/verify")
    public ResponseEntity<UserDTO> verifyOtp(@RequestBody OtpVerifyRequestDTO request) {
        return ResponseEntity.ok(otpService.verifyOtp(request));
    }
}

