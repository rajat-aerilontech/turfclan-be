package com.aerilon.turfclan.user.service.impl;

import com.aerilon.turfclan.exception.InvalidRequestException;
import com.aerilon.turfclan.exception.ResourceNotFoundException;
import com.aerilon.turfclan.jwt.JwtProperties;
import com.aerilon.turfclan.jwt.JwtService;
import com.aerilon.turfclan.user.dto.TokenRefreshRequestDTO;
import com.aerilon.turfclan.user.dto.TokenRefreshResponseDTO;
import com.aerilon.turfclan.user.entity.UserEntity;
import com.aerilon.turfclan.user.repository.UserRepository;
import com.aerilon.turfclan.user.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;


@Service
@Slf4j
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final JwtService jwtService;
    private final JwtProperties jwtProperties;
    private final UserRepository userRepository;

    @Override
    public TokenRefreshResponseDTO refresh(TokenRefreshRequestDTO request) {
        String refreshToken = request.getRefreshToken();

        if (refreshToken == null || refreshToken.isBlank()) {
            throw new InvalidRequestException("Refresh token must not be blank");
        }

        // Validate and parse the incoming refresh token
        String userId;
        try {
            userId = jwtService.getSubject(refreshToken);
        } catch (RuntimeException ex) {
            log.warn("Invalid or expired refresh token: {}", ex.getMessage());
            throw new InvalidRequestException("Invalid or expired refresh token");
        }

        // Confirm the user still exists and is active
        UserEntity user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new ResourceNotFoundException("User not found for id: " + userId));

        // Build access token claims
        Map<String, Object> accessClaims = Map.of(
                "phoneNumber", user.getPhoneNumber(),
                "userName", user.getUserName()
        );

        // Issue new tokens (rotation — old refresh token is effectively discarded)
        String newAccessToken = jwtService.generateAccessToken(userId, accessClaims);
        String newRefreshToken = jwtService.generateRefreshToken(userId);
        long expiresIn = jwtProperties.getAccessTokenExpiryMs() / 1000;

        log.info("Tokens refreshed for user id: {}", userId);

        return TokenRefreshResponseDTO.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .expiresIn(expiresIn)
                .build();
    }
}
