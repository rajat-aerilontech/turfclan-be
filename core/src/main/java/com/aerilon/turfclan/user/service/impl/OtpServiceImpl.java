package com.aerilon.turfclan.user.service.impl;

import com.aerilon.turfclan.exception.InvalidRequestException;
import com.aerilon.turfclan.user.dto.OtpRequestDTO;
import com.aerilon.turfclan.user.dto.OtpResponseDTO;
import com.aerilon.turfclan.user.entity.OtpEntity;
import com.aerilon.turfclan.user.repository.OtpRepository;
import com.aerilon.turfclan.user.service.OtpService;
import com.aerilon.turfclan.user.entity.UserEntity;
import com.aerilon.turfclan.user.enums.UserStatus;
import com.aerilon.turfclan.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
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

    @Override
    @Transactional
    public OtpResponseDTO requestOtp(OtpRequestDTO request) {
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
            newUser.setPhoneCountryCode(request.getPhoneCountryCode());
            newUser.setUserName("user_" + UUID.randomUUID().toString().replace("-", "").substring(0, 12));
            newUser.setStatus(UserStatus.PENDING_VERIFICATION);
            newUser.setCreatedBy(LocalDateTime.now());
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
                .newUser(isNewUser)
                .oldUser(!isNewUser)
                .message("OTP sent successfully")
                .expiresAt(expiresAt)
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
