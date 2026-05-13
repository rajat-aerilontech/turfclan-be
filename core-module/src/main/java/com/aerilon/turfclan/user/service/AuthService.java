package com.aerilon.turfclan.user.service;

import com.aerilon.turfclan.user.dto.TokenRefreshRequestDTO;
import com.aerilon.turfclan.user.dto.TokenRefreshResponseDTO;

import com.aerilon.turfclan.user.dto.UserDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.aerilon.turfclan.user.dto.DeviceSessionDTO;
import java.util.List;
import java.util.UUID;

public interface AuthService {
    TokenRefreshResponseDTO refresh(TokenRefreshRequestDTO request, HttpServletRequest httpRequest, HttpServletResponse httpResponse);
    void logout(HttpServletRequest httpRequest, HttpServletResponse httpResponse, String accessToken);
    void logoutAll(String userId);
    List<DeviceSessionDTO> getSessions(String userId);
    void revokeSession(UUID sessionId, String userId);
    UserDTO getMe(String userId, String accessToken);
}
