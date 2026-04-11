package com.aerilon.turfclan.user.service;

import com.aerilon.turfclan.user.dto.TokenRefreshRequestDTO;
import com.aerilon.turfclan.user.dto.TokenRefreshResponseDTO;

public interface AuthService {
    TokenRefreshResponseDTO refresh(TokenRefreshRequestDTO request);
}
