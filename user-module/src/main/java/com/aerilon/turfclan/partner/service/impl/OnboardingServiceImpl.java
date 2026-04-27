package com.aerilon.turfclan.partner.service.impl;

import com.aerilon.turfclan.partner.dto.*;
import com.aerilon.turfclan.partner.entity.*;
import com.aerilon.turfclan.partner.enums.OnboardApplicationStatus;
import com.aerilon.turfclan.partner.enums.OnboardStep;
import com.aerilon.turfclan.partner.converter.*;
import com.aerilon.turfclan.partner.repository.*;
import com.aerilon.turfclan.partner.service.OnboardingService;
import com.aerilon.turfclan.user.entity.UserEntity;
import com.aerilon.turfclan.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class OnboardingServiceImpl implements OnboardingService {

    private final UserRepository userRepository;
    private final OnboardingApplicationRepository applicationRepository;
    private final BusinessDetailRepository businessDetailRepository;
    private final BrandDetailRepository brandDetailRepository;
    private final HelpUsRepository helpUsRepository;
    private final FacilityRepository facilityRepository;
    private final SportDetailRepository sportDetailRepository;
    private final PartnerDetailRepository partnerDetailRepository;
    private final BankDetailRepository bankDetailRepository;
    private final OnboardingContractRepository contractRepository;
    
    // Converters
    private final BusinessDetailConverter businessDetailConverter;
    private final BrandDetailConverter brandDetailConverter;
    private final HelpUsConverter helpUsConverter;
    private final FacilityConverter facilityConverter;
    private final SportDetailConverter sportDetailConverter;
    private final PartnerDetailConverter partnerDetailConverter;
    private final BankDetailConverter bankDetailConverter;
    private final ContractConverter contractConverter;

    @Override
    public OnboardStep getCurrentOnboardingStep(String userId) {
        UserEntity user = getUser(userId);
        return applicationRepository.findByUserId(user)
                .map(OnboardingApplicationEntity::getCurrentStep)
                .orElse(OnboardStep.BUSINESS_DETAILS);
    }

    @Override
    @Transactional
    public void saveBusinessInfo(String userId, BusinessInfoDto dto) {
        UserEntity user = getUser(userId);
        OnboardingApplicationEntity app = createApplication(user);

        // Business Detail
        BusinessDetailEntity businessEntity = businessDetailConverter.convert(dto);
        if (businessEntity != null) {
            businessEntity.setUserId(user);
            businessEntity.setApplicationId(app);
            businessDetailRepository.save(businessEntity);
        }

        // Brand Detail
        BrandDetailEntity brandEntity = brandDetailConverter.convert(dto);
        if (brandEntity != null) {
            brandEntity.setUserId(user);
            brandEntity.setApplicationId(app);
            brandDetailRepository.save(brandEntity);
        }

        // Help Us Detail
        HelpUsEntity helpUsEntity = helpUsConverter.convert(dto);
        if (helpUsEntity != null) {
            helpUsEntity.setUserId(user);
            helpUsEntity.setApplicationId(app);
            helpUsRepository.save(helpUsEntity);
        }

        app.setCurrentStep(OnboardStep.SPORT_DETAILS);
        applicationRepository.save(app);
    }

    @Override
    @Transactional
    public void saveFacilityInfo(String userId, FacilitiesDto dto) {
        UserEntity user = getUser(userId);
        OnboardingApplicationEntity app = getOnboardingApplication(user);

        // Clear existing facilities if needed, or simply append. For now, assuming new addition.
        if (dto.getFacilities() != null) {
            for (FacilityDto facilityDto : dto.getFacilities()) {
                FacilityEntity facilityEntity = facilityConverter.convert(facilityDto);
                if (facilityEntity != null) {
                    facilityEntity.setUserId(user);
                    facilityEntity.setApplication(app);

                    // Initialize sports list
                    List<SportDetailEntity> sports = new ArrayList<>();
                    if (facilityDto.getSports() != null) {
                        for (SportDetailDto sportDto : facilityDto.getSports()) {
                            SportDetailEntity sportEntity = sportDetailConverter.convert(sportDto);
                            if (sportEntity != null) {
                                sportEntity.setFacility(facilityEntity);
                                sports.add(sportEntity);
                            }
                        }
                    }
                    facilityEntity.setSports(sports);
                    facilityRepository.save(facilityEntity);
                }
            }
        }

        app.setCurrentStep(OnboardStep.PARTNER_DETAILS);
        applicationRepository.save(app);
    }

    @Override
    @Transactional
    public void savePartnerDetails(String userId, PartnerDetailDto dto) {
        UserEntity user = getUser(userId);
        OnboardingApplicationEntity app = getOnboardingApplication(user);

        PartnerDetailEntity entity = partnerDetailConverter.convert(dto);
        if (entity != null) {
            entity.setUserId(user);
            entity.setApplication(app);
            partnerDetailRepository.save(entity);
        }

        app.setCurrentStep(OnboardStep.BANK_DETAILS);
        applicationRepository.save(app);
    }

    @Override
    @Transactional
    public void saveBankDetails(String userId, BankDetailDto dto) {
        UserEntity user = getUser(userId);
        OnboardingApplicationEntity app = getOnboardingApplication(user);

        BankDetailEntity entity = bankDetailConverter.convert(dto);
        if (entity != null) {
            entity.setUserId(user);
            entity.setApplicationId(app);
            bankDetailRepository.save(entity);
        }

        app.setCurrentStep(OnboardStep.CONTRACT);
        applicationRepository.save(app);
    }

    @Override
    @Transactional
    public void signContract(String userId, ContractDto dto, HttpServletRequest request) {
        UserEntity user = getUser(userId);
        OnboardingApplicationEntity app = getOnboardingApplication(user);

        OnboardingContractEntity entity = contractConverter.convert(dto);
        if (entity != null) {
            entity.setUserId(user);
            entity.setApplication(app);
            
            // Extract IP address from request
            String ipAddress = request.getHeader("X-Forwarded-For");
            if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getRemoteAddr();
            }
            entity.setIpAddress(ipAddress);
            entity.setSignedAt(LocalDateTime.now());
            
            contractRepository.save(entity);
        }

        app.setCurrentStep(OnboardStep.REVIEW_FORM);
        app.setIsSubmitted(true);
        applicationRepository.save(app);
    }

    @Override
    public void submitApplication(String userId) {
        UserEntity user = getUser(userId);
        OnboardingApplicationEntity app = getOnboardingApplication(user);

        app.setCurrentStep(OnboardStep.REVIEW_FORM);
        app.setIsSubmitted(true);
        applicationRepository.save(app);
    }

    private OnboardingApplicationEntity createApplication(UserEntity user) {
        OnboardingApplicationEntity app = new OnboardingApplicationEntity();
        app.setUserId(user);
        app.setOnboardApplicationStatus(OnboardApplicationStatus.DRAFT);
        app.setCurrentStep(OnboardStep.BUSINESS_DETAILS);
        app.setIsSubmitted(false);
        app.setCreatedAt(LocalDateTime.now());
        return applicationRepository.save(app);
    }

    private UserEntity getUser(String userId) {
        return userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private OnboardingApplicationEntity getOnboardingApplication(UserEntity user) {
        return applicationRepository.findByUserId(user)
                .orElseThrow(() -> new RuntimeException("Onboarding Application not found"));
    }
}
