package com.aerilon.turfclan.user.service.impl;

import com.aerilon.turfclan.exception.InvalidRequestException;
import com.aerilon.turfclan.exception.ResourceNotFoundException;
import com.aerilon.turfclan.jwt.JwtProperties;
import com.aerilon.turfclan.jwt.JwtService;
import com.aerilon.turfclan.user.converter.UserEntityToUserDTOConverter;
import com.aerilon.turfclan.user.dto.AuthResponseDTO;
import com.aerilon.turfclan.user.dto.SendOtpRequestDTO;
import com.aerilon.turfclan.user.dto.OtpResponseDTO;
import com.aerilon.turfclan.user.dto.OtpVerifyRequestDTO;
import com.aerilon.turfclan.user.dto.UserDTO;
import com.aerilon.turfclan.user.entity.OtpEntity;
import com.aerilon.turfclan.user.repository.OtpRepository;
import com.aerilon.turfclan.user.service.OtpService;
import com.aerilon.turfclan.user.entity.UserEntity;
import com.aerilon.turfclan.user.enums.UserStatus;
import com.aerilon.turfclan.user.enums.UserRole;
import com.aerilon.turfclan.partner.entity.OnboardingApplicationEntity;
import com.aerilon.turfclan.partner.enums.OnboardApplicationStatus;
import com.aerilon.turfclan.partner.enums.OnboardStep;
import com.aerilon.turfclan.partner.repository.OnboardingApplicationRepository;
import com.aerilon.turfclan.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.redis.core.RedisTemplate;
import com.aerilon.turfclan.user.entity.DeviceSessionEntity;
import com.aerilon.turfclan.user.repository.DeviceSessionRepository;
import com.aerilon.turfclan.util.CookieUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.codec.digest.DigestUtils;
import java.time.Duration;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class OtpServiceImpl implements OtpService {

    private static final int OTP_EXPIRY_MINUTES = 10;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final UserRepository userRepository;
    private final OtpRepository otpRepository;
    private final UserEntityToUserDTOConverter userConverter;
    private final JwtService jwtService;
    private final JwtProperties jwtProperties;
    private final OnboardingApplicationRepository applicationRepository;
    private final DeviceSessionRepository deviceSessionRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    @Transactional
    public OtpResponseDTO requestOtp(SendOtpRequestDTO request, String sourceApp) {
        String phoneNumber = request.getPhoneNumber();

        if (phoneNumber == null || phoneNumber.isBlank()) {
            throw new InvalidRequestException("Phone number must not be blank");
        }

        Optional<UserEntity> existingUser = userRepository.findByPhoneNumber(phoneNumber);

        boolean isNewUser = existingUser.isEmpty();

        if (isNewUser) {
            log.info("No user found for phone number ending in {}. Creating new user.", maskPhone(phoneNumber));
            UserEntity newUser = new UserEntity();
            newUser.setPhoneNumber(phoneNumber);
            newUser.setCountryIsoCode("IN");
            newUser.setPhoneCountryCode(request.getPhoneCountryCode());
            newUser.setUserName(UUID.randomUUID().toString().replace("-", "").substring(0, 12));
            newUser.setStatus(UserStatus.PENDING_VERIFICATION);
            newUser.setProfileComplete(false);
            newUser.setCreatedAt(LocalDateTime.now());
            if ("turf-partner".equalsIgnoreCase(sourceApp)) {
                newUser.setUserRole(UserRole.PARTNER);
            } else {
                newUser.setUserRole(UserRole.USER);
            }
            userRepository.save(newUser);
        } else {
            log.info("Existing user found for phone number ending in {}.", maskPhone(phoneNumber));
        }

        String otpCode = generateOtp();
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES);

        OtpEntity otpEntity = new OtpEntity();
        otpEntity.setPhoneNumber(phoneNumber);
        otpEntity.setOtpCode(otpCode);
        otpEntity.setExpiresAt(expiresAt);
        otpEntity.setUsed(false);
        otpEntity.setCreatedAt(LocalDateTime.now());
        otpRepository.save(otpEntity);

        log.info("OTP generated and stored for phone number ending in {}.", maskPhone(phoneNumber));

        return OtpResponseDTO.builder()
                .message("OTP sent successfully")
                .expiresAt(expiresAt)
                .build();
    }

    @Override
    @Transactional
    public AuthResponseDTO verifyOtp(OtpVerifyRequestDTO request, String sourceApp, HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        String phoneNumber = request.getPhoneNumber();
        String otpCode = request.getOtpCode();
        if (phoneNumber == null || phoneNumber.isBlank()) {
            throw new InvalidRequestException("Phone number must not be blank");
        }
        if (otpCode == null || otpCode.isBlank()) {
            throw new InvalidRequestException("OTP code must not be blank");
        }
        UserEntity user = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new ResourceNotFoundException("No user found for the given phone number"));
        OtpEntity otpEntity = otpRepository
                .findTopByPhoneNumberAndIsUsedFalseAndExpiresAtAfterOrderByCreatedAtDesc(phoneNumber, LocalDateTime.now())
                .orElseThrow(() -> new InvalidRequestException("Invalid or expired OTP"));
        if (!otpEntity.getOtpCode().equals(otpCode)) {
            throw new InvalidRequestException("Invalid OTP code");
        }
        otpEntity.setUsed(true);
        otpRepository.save(otpEntity);

        boolean isProfileComplete = user.isProfileComplete();
        if (user.getUserName() == null || user.getUserName().isBlank()) {
            user.setUserName(UUID.randomUUID().toString().replace("-", "").substring(0, 12));
        }
        user.setStatus(isProfileComplete ? UserStatus.ACTIVE : UserStatus.PENDING_SIGNUP);
        user.setVerified(true);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        log.info("OTP verified successfully for phone number ending in {}.", maskPhone(phoneNumber));

        String userId = user.getId().toString();
        boolean isNewUser = !isProfileComplete;

        if (isNewUser && "partner".equalsIgnoreCase(sourceApp)) {
            Optional<OnboardingApplicationEntity> existingApp = applicationRepository.findByUser(user);
            if (existingApp.isEmpty()) {
                OnboardingApplicationEntity app = new OnboardingApplicationEntity();
                app.setUser(user);
                app.setOnboardApplicationStatus(OnboardApplicationStatus.DRAFT);
                app.setCurrentStep(OnboardStep.BUSINESS_DETAILS);
                app.setIsSubmitted(false);
                applicationRepository.save(app);
            }
        }

        Map<String, Object> claims = new java.util.HashMap<>();
        claims.put("phoneNumber", user.getPhoneNumber());
        claims.put("userName", user.getUserName());
        if (user.getUserRole() != null) {
            String roleName = user.getUserRole().name();
            String simpleRole = roleName.substring(0, 1).toUpperCase() + roleName.substring(1).toLowerCase();
            claims.put("aud", simpleRole);
        }

        String refreshToken = jwtService.generateRefreshToken(userId);
        long expiresIn = jwtProperties.getAccessTokenExpiryMs() / 1000;

        // Hash the refresh token
        String tokenHash = DigestUtils.sha256Hex(refreshToken);
        
        // Build Device Session
        String userAgent = httpRequest.getHeader("User-Agent");
        String ipAddress = httpRequest.getRemoteAddr();
        
        DeviceSessionEntity sessionEntity = DeviceSessionEntity.builder()
                .userId(user.getId())
                .refreshTokenHash(tokenHash)
                .platform(httpRequest.getServletPath().startsWith("/api/v1/web/") ? "WEB" : "MOBILE")
                .ipAddress(ipAddress)
                .userAgent(userAgent != null && userAgent.length() > 500 ? userAgent.substring(0, 500) : userAgent)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plus(jwtProperties.getRefreshTokenExpiryMs(), ChronoUnit.MILLIS))
                .revoked(false)
                .build();
        deviceSessionRepository.save(sessionEntity);
        
        claims.put("sessionId", sessionEntity.getSessionId().toString());
        String accessToken = jwtService.generateAccessToken(userId, claims);

        // Save to Redis
        String redisKey = "session:" + sessionEntity.getSessionId();
        redisTemplate.opsForValue().set(redisKey, user.getId().toString(), Duration.ofMillis(jwtProperties.getRefreshTokenExpiryMs()));
        
        // Append HTTP-Only cookie for Web clients
        if (httpRequest.getServletPath().startsWith("/api/v1/web/")) {
            CookieUtil.createHttpOnlyCookie(httpResponse, CookieUtil.REFRESH_TOKEN_COOKIE_NAME, refreshToken, (int) (jwtProperties.getRefreshTokenExpiryMs() / 1000));
        }

        UserDTO userDTO = userConverter.convert(user);

        return AuthResponseDTO.builder()
                .user(userDTO)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(expiresIn)
                .newUser(isNewUser)
                .oldUser(isProfileComplete)
                .message("OTP verified successfully")
                .build();
    }

    private String generateOtp() {
        int otp = 100000 + SECURE_RANDOM.nextInt(900000);
        return String.valueOf(otp);
    }

    private String maskPhone(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.length() < 4) {
            return "****";
        }
        return "****" + phoneNumber.substring(phoneNumber.length() - 4);
    }
}
