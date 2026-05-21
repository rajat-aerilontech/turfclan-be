package com.aerilon.turfclan.user.service.impl;

import com.aerilon.turfclan.exception.InvalidRequestException;
import com.aerilon.turfclan.exception.ResourceNotFoundException;
import com.aerilon.turfclan.exception.UnauthorizedAccessException;
import com.aerilon.turfclan.jwt.JwtProperties;
import com.aerilon.turfclan.jwt.JwtService;
import com.aerilon.turfclan.user.converter.UserEntityToUserDTOConverter;
import com.aerilon.turfclan.user.dto.UserDTO;
import com.aerilon.turfclan.user.dto.DeviceSessionDTO;
import com.aerilon.turfclan.user.dto.TokenRefreshRequestDTO;
import com.aerilon.turfclan.user.dto.TokenRefreshResponseDTO;
import com.aerilon.turfclan.user.entity.DeviceSessionEntity;
import com.aerilon.turfclan.user.entity.UserEntity;
import com.aerilon.turfclan.user.enums.UserRole;
import com.aerilon.turfclan.user.repository.DeviceSessionRepository;
import com.aerilon.turfclan.user.repository.UserRepository;
import com.aerilon.turfclan.user.service.AuthService;
import com.aerilon.turfclan.util.CookieUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final JwtService jwtService;
    private final JwtProperties jwtProperties;
    private final UserRepository userRepository;
    private final DeviceSessionRepository deviceSessionRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final UserEntityToUserDTOConverter userConverter;

    @Override
    @Transactional
    public TokenRefreshResponseDTO refresh(TokenRefreshRequestDTO request, HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        boolean extractedFromCookie = false;
        String refreshToken = refreshToken = httpRequest.getHeader("refresh-token");

        if (refreshToken == null || refreshToken.isBlank()) {
            refreshToken = CookieUtil.extractCookie(httpRequest, CookieUtil.REFRESH_TOKEN_COOKIE_NAME);
            if (refreshToken != null && !refreshToken.isBlank()) {
                extractedFromCookie = true;
            }
        }

        if (refreshToken == null || refreshToken.isBlank()) {
            throw new InvalidRequestException("Refresh token must not be blank");
        }

        String userId;
        try {
            userId = jwtService.getSubject(refreshToken);
        } catch (RuntimeException ex) {
            log.warn("Invalid or expired refresh token: {}", ex.getMessage());
            throw new UnauthorizedAccessException("Invalid or expired refresh token");
        }

        String tokenHash = DigestUtils.sha256Hex(refreshToken);
        String graceKey = "grace_refresh:" + tokenHash;
        
        Object cachedObj = redisTemplate.opsForValue().get(graceKey);
        if (cachedObj != null) {
            return parseGracePeriodResponse(cachedObj, httpRequest, httpResponse);
        }
        
        DeviceSessionEntity session = deviceSessionRepository.findByRefreshTokenHash(tokenHash)
                .orElse(null);

        if (session == null) {
            // Check cache one more time in case a concurrent request just finished
            cachedObj = redisTemplate.opsForValue().get(graceKey);
            if (cachedObj != null) {
                return parseGracePeriodResponse(cachedObj, httpRequest, httpResponse);
            }
            // Reuse attack detected, revoke all sessions
            log.warn("Reuse attack detected for user: {}", userId);
            logoutAll(userId);
            throw new UnauthorizedAccessException("Invalid refresh token. All sessions revoked for security.");
        }

        if (session.isRevoked() || session.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new UnauthorizedAccessException("Refresh token is revoked or expired");
        }

        UserEntity user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new ResourceNotFoundException("User not found for id: " + userId));

        boolean isWeb = user.getUserRole() != null && (user.getUserRole() == UserRole.PARTNER || user.getUserRole() == UserRole.ADMIN);

        if (!isWeb && extractedFromCookie) {
            throw new UnauthorizedAccessException("Mobile clients must not use cookie-based authentication");
        }

        Map<String, Object> accessClaims = new java.util.HashMap<>();
        accessClaims.put("phoneNumber", user.getPhoneNumber());
        accessClaims.put("userName", user.getUserName());
        accessClaims.put("sessionId", session.getSessionId().toString());
        if (user.getUserRole() != null) {
            String roleName = user.getUserRole().name();
            String simpleRole = roleName.substring(0, 1).toUpperCase() + roleName.substring(1).toLowerCase();
            accessClaims.put("aud", simpleRole);
        }

        String newAccessToken = jwtService.generateAccessToken(userId, accessClaims);
        String newRefreshToken = jwtService.generateRefreshToken(userId);
        String newTokenHash = DigestUtils.sha256Hex(newRefreshToken);
        long expiresIn = jwtProperties.getAccessTokenExpiryMs() / 1000;

        session.setRefreshTokenHash(newTokenHash);
        session.setLastUsedAt(LocalDateTime.now());
        session.setExpiresAt(LocalDateTime.now().plus(jwtProperties.getRefreshTokenExpiryMs(), ChronoUnit.MILLIS));
        session.setPlatform(isWeb ? "WEB" : "MOBILE");
        deviceSessionRepository.save(session);
        
        String redisKey = "session:" + session.getSessionId();
        redisTemplate.opsForValue().set(redisKey, user.getId().toString(), Duration.ofMillis(jwtProperties.getRefreshTokenExpiryMs()));

        if (isWeb) {
            CookieUtil.createHttpOnlyCookie(httpResponse, CookieUtil.REFRESH_TOKEN_COOKIE_NAME, newRefreshToken, (int) (jwtProperties.getRefreshTokenExpiryMs() / 1000));
        } else {
            // Clean up cookie for mobile or non-web clients if one was sent (e.g. by simulated/browser clients)
            if (CookieUtil.extractCookie(httpRequest, CookieUtil.REFRESH_TOKEN_COOKIE_NAME) != null) {
                CookieUtil.clearCookie(httpResponse, CookieUtil.REFRESH_TOKEN_COOKIE_NAME);
            }
        }

        log.info("Tokens refreshed for user id: {}", userId);

        TokenRefreshResponseDTO responseDTO = TokenRefreshResponseDTO.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .expiresIn(expiresIn)
                .build();
                
        String cacheValue = newAccessToken + "::" + newRefreshToken + "::" + expiresIn + "::" + (isWeb ? "WEB" : "MOBILE");
        redisTemplate.opsForValue().set(graceKey, cacheValue, Duration.ofSeconds(45));

        return responseDTO;
    }

    @Override
    @Transactional
    public void logout(HttpServletRequest httpRequest, HttpServletResponse httpResponse, String accessToken) {
        String refreshToken = CookieUtil.extractCookie(httpRequest, CookieUtil.REFRESH_TOKEN_COOKIE_NAME);
        if (refreshToken != null) {
            String tokenHash = DigestUtils.sha256Hex(refreshToken);
            deviceSessionRepository.findByRefreshTokenHash(tokenHash).ifPresent(session -> {
                session.setRevoked(true);
                session.setRevokedAt(LocalDateTime.now());
                deviceSessionRepository.save(session);
                redisTemplate.delete("session:" + session.getSessionId());
            });
            CookieUtil.clearCookie(httpResponse, CookieUtil.REFRESH_TOKEN_COOKIE_NAME);
        }
        
        if (accessToken != null && accessToken.startsWith("Bearer ")) {
            String token = accessToken.substring(7);
            try {
                jwtService.validateToken(token);
                redisTemplate.opsForValue().set("revoked_access:" + token, "revoked", Duration.ofMillis(jwtProperties.getAccessTokenExpiryMs()));
            } catch (Exception ignored) {
            }
        }
    }

    @Override
    @Transactional
    public void logoutAll(String userId) {
        List<DeviceSessionEntity> sessions = deviceSessionRepository.findAllByUserIdAndRevokedFalse(UUID.fromString(userId));
        for (DeviceSessionEntity session : sessions) {
            session.setRevoked(true);
            session.setRevokedAt(LocalDateTime.now());
            redisTemplate.delete("session:" + session.getSessionId());
        }
        deviceSessionRepository.saveAll(sessions);
        // Note: Hard to blacklist all active JWTs unless we maintain a user mapping, 
        // usually rely on their short expiry. For strictness, could add a user_revoked_before timestamp in Redis.
    }

    @Override
    public List<DeviceSessionDTO> getSessions(String userId) {
        return deviceSessionRepository.findAllByUserIdAndRevokedFalse(UUID.fromString(userId))
                .stream().map(session -> DeviceSessionDTO.builder()
                        .sessionId(session.getSessionId())
                        .deviceId(session.getDeviceId())
                        .deviceName(session.getDeviceName())
                        .platform(session.getPlatform())
                        .ipAddress(session.getIpAddress())
                        .userAgent(session.getUserAgent())
                        .createdAt(session.getCreatedAt())
                        .lastUsedAt(session.getLastUsedAt())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void revokeSession(UUID sessionId, String userId) {
        deviceSessionRepository.findById(sessionId).ifPresent(session -> {
            if (session.getUserId().toString().equals(userId)) {
                session.setRevoked(true);
                session.setRevokedAt(LocalDateTime.now());
                deviceSessionRepository.save(session);
                redisTemplate.delete("session:" + session.getSessionId());
            }
        });
    }

    @Override
    public UserDTO getMe(String userId, String accessToken) {
        if (accessToken != null && accessToken.startsWith("Bearer ")) {
            String token = accessToken.substring(7);
            try {
                io.jsonwebtoken.Claims claims = jwtService.validateToken(token);
                String sessionId = claims.get("sessionId", String.class);
                if (sessionId != null) {
                    Boolean hasKey = redisTemplate.hasKey("session:" + sessionId);
                    if (Boolean.FALSE.equals(hasKey)) {
                        throw new UnauthorizedAccessException("Session has been revoked or expired");
                    }
                }
            } catch (Exception ex) {
                throw new UnauthorizedAccessException("Invalid or expired access token");
            }
        }

        UserEntity user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
                
        return userConverter.convert(user);
    }

    private TokenRefreshResponseDTO parseGracePeriodResponse(Object cachedObj, HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        log.info("Concurrent refresh detected. Returning grace period tokens.");
        String[] parts = cachedObj.toString().split("::");
        if (parts.length >= 3) {
            String cachedAccess = parts[0];
            String cachedRefresh = parts[1];
            long cachedExpiresIn = Long.parseLong(parts[2]);
            String platform = parts.length > 3 ? parts[3] : "WEB";
            
            if ("WEB".equals(platform)) {
                CookieUtil.createHttpOnlyCookie(httpResponse, CookieUtil.REFRESH_TOKEN_COOKIE_NAME, cachedRefresh, (int) (jwtProperties.getRefreshTokenExpiryMs() / 1000));
            } else {
                if (CookieUtil.extractCookie(httpRequest, CookieUtil.REFRESH_TOKEN_COOKIE_NAME) != null) {
                    CookieUtil.clearCookie(httpResponse, CookieUtil.REFRESH_TOKEN_COOKIE_NAME);
                }
            }
            return TokenRefreshResponseDTO.builder()
                    .accessToken(cachedAccess)
                    .refreshToken(cachedRefresh)
                    .expiresIn(cachedExpiresIn)
                    .build();
        }
        throw new UnauthorizedAccessException("Invalid grace period cache");
    }
}
