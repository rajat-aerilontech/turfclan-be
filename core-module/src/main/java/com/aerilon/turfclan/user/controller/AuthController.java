package com.aerilon.turfclan.user.controller;

import com.aerilon.turfclan.user.dto.DeviceSessionDTO;
import com.aerilon.turfclan.user.dto.TokenRefreshRequestDTO;
import com.aerilon.turfclan.user.dto.TokenRefreshResponseDTO;
import com.aerilon.turfclan.user.service.AuthService;
import com.aerilon.turfclan.security.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import com.aerilon.turfclan.user.dto.UserDTO;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Token and device session lifecycle APIs")
public class AuthController {

    private final AuthService authService;
    private final SecurityUtils securityUtils;

    @PostMapping("/refresh")
    @PreAuthorize("hasAnyAuthority('ROLE_TM_USER', 'ROLE_TM_PARTNER', 'ROLE_TA_USER')")
    @Operation(summary = "Refresh Token", description = "Issues a new access token and rotates the refresh token. Can read from request body or HttpOnly cookie.")
    public ResponseEntity<TokenRefreshResponseDTO> refresh(
            @RequestBody(required = false) TokenRefreshRequestDTO request,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse) {
        return ResponseEntity.ok(authService.refresh(request, httpRequest, httpResponse));
    }

    @PostMapping("/logout")
    @PreAuthorize("hasAnyAuthority('ROLE_TM_USER', 'ROLE_TM_PARTNER', 'ROLE_TA_USER')")
    @Operation(summary = "Logout Current Device", description = "Revokes the current session and clears the refresh token cookie if present.")
    public ResponseEntity<Void> logout(
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse,
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authHeader) {
        authService.logout(httpRequest, httpResponse, authHeader);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/logout-all")
    @PreAuthorize("hasAnyAuthority('ROLE_TM_USER', 'ROLE_TM_PARTNER', 'ROLE_TA_USER')")
    @Operation(summary = "Logout All Devices", description = "Revokes all active sessions for the authenticated user.")
    public ResponseEntity<Void> logoutAll() {
        String userId = securityUtils.getCurrentUserId();
        authService.logoutAll(userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/sessions")
    @PreAuthorize("hasAnyAuthority('ROLE_TM_USER', 'ROLE_TM_PARTNER')")
    @Operation(summary = "Get Active Sessions", description = "Returns a list of all active device sessions for the authenticated user.")
    public ResponseEntity<List<DeviceSessionDTO>> getSessions() {
        String userId = securityUtils.getCurrentUserId();
        return ResponseEntity.ok(authService.getSessions(userId));
    }

    @DeleteMapping("/sessions/{sessionId}")
    @PreAuthorize("hasAnyAuthority('ROLE_TM_USER', 'ROLE_TM_PARTNER')")
    @Operation(summary = "Revoke Specific Session", description = "Revokes a specific device session by its ID.")
    public ResponseEntity<Void> revokeSession(@PathVariable UUID sessionId) {
        String userId = securityUtils.getCurrentUserId();
        authService.revokeSession(sessionId, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    @PreAuthorize("hasAnyAuthority('ROLE_TM_USER', 'ROLE_TM_PARTNER')")
    @Operation(summary = "Get Current User", description = "Restores authenticated session, validates against Redis, and returns the current user profile.")
    public ResponseEntity<UserDTO> getMe(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authHeader) {
        String userId = securityUtils.getCurrentUserId();
        return ResponseEntity.ok(authService.getMe(userId, authHeader));
    }
}
