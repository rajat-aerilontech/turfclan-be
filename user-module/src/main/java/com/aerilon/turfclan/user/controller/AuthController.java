package com.aerilon.turfclan.user.controller;

import com.aerilon.turfclan.user.dto.TokenRefreshRequestDTO;
import com.aerilon.turfclan.user.dto.TokenRefreshResponseDTO;
import com.aerilon.turfclan.user.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Token lifecycle APIs")
public class AuthController {

    private final AuthService authService;

    /**
     * Issues a new access token (and rotates the refresh token) given a valid refresh JWT.
     *
     * POST /api/v1/auth/refresh
     * Body: { "refreshToken": "<jwt>" }
     */
    @PostMapping("/refresh")
    @PreAuthorize("hasAuthority('ROLE_TM_USER')")
    @Operation(summary = "Refresh Token", description = "Issues a new access token and rotates the refresh token given a valid refresh JWT.")
    public ResponseEntity<TokenRefreshResponseDTO> refresh(@RequestBody TokenRefreshRequestDTO request) {
        return ResponseEntity.ok(authService.refresh(request));
    }
}
