package com.aerilon.turfclan.partner.service.impl;

import com.aerilon.turfclan.exception.InvalidRequestException;
import com.aerilon.turfclan.facility.converter.FacilityConverter;
import com.aerilon.turfclan.facility.converter.SportDetailConverter;
import com.aerilon.turfclan.facility.dto.FacilitiesRequestDto;
import com.aerilon.turfclan.facility.dto.FacilityRequestDto;
import com.aerilon.turfclan.facility.dto.SportDetailRequestDto;
import com.aerilon.turfclan.facility.entity.FacilityEntity;
import com.aerilon.turfclan.facility.entity.SportDetailEntity;
import com.aerilon.turfclan.partner.dto.*;
import com.aerilon.turfclan.partner.entity.*;
import com.aerilon.turfclan.partner.enums.OnboardApplicationStatus;
import com.aerilon.turfclan.partner.enums.OnboardStep;
import com.aerilon.turfclan.partner.converter.*;
import com.aerilon.turfclan.partner.enums.SignatureType;
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
        return applicationRepository.findByUser(user)
                .map(OnboardingApplicationEntity::getCurrentStep)
                .orElse(OnboardStep.BUSINESS_DETAILS);
    }

    @Override
    public OnboardingFullDataDto getFullOnboardingData(String userId) {
        UserEntity user = getUser(userId);
        OnboardingApplicationEntity app = getOnboardingApplication(user);
        BusinessInfoDto businessInfo = mapToBusinessInfoDto(user);
        List<FacilityRequestDto> facilities = facilityRepository.findByUser(user).stream()
                .map(facilityConverter::toDto)
                .toList();
        PartnerDetailRequestDto partnerDetails = partnerDetailRepository.findByUser(user)
                .map(partnerDetailConverter::toDto)
                .orElse(null);
        BankDetailRequestDto bankDetails = bankDetailRepository.findByUser(user)
                .map(bankDetailConverter::toDto)
                .orElse(null);
        ContractRequestDto contractDetails = contractRepository.findByUser(user)
                .map(contractConverter::toDto)
                .orElse(null);

        return OnboardingFullDataDto.builder()
                .status(app.getOnboardApplicationStatus())
                .currentStep(app.getCurrentStep())
                .isSubmitted(app.getIsSubmitted())
                .businessInfo(businessInfo)
                .facilities(facilities)
                .partnerDetails(partnerDetails)
                .bankDetails(bankDetails)
                .contractDetails(contractDetails)
                .build();
    }

    private BusinessInfoDto mapToBusinessInfoDto(UserEntity user) {
        BusinessDetailEntity businessDetailEntity = businessDetailRepository.findByUser(user);
        BrandDetailEntity brandEntity = brandDetailRepository.findByUser(user);
        HelpUsEntity helpEntity = helpUsRepository.findByUser(user).orElse(null);
        if (businessDetailEntity == null && brandEntity == null && helpEntity == null) return null;
        BusinessInfoDto businessInfoDto = new BusinessInfoDto();
        if (businessDetailEntity != null) {
            businessInfoDto.setBusinessDetail(businessDetailConverter.toDto(businessDetailEntity));
        }
        if (brandEntity != null) {
            businessInfoDto.setBrandDetails(brandDetailConverter.toDto(brandEntity));
        }
        if (helpEntity != null) {
            businessInfoDto.setHelpUsDetail(helpUsConverter.toDto(helpEntity));
        }
        return businessInfoDto;
    }

    @Override
    @Transactional
    public void saveBusinessInfo(String userId, BusinessInfoDto dto) {
        UserEntity user = getUser(userId);
        OnboardingApplicationEntity app = createApplication(user);

        // Business Detail
        BusinessDetailEntity businessEntity = businessDetailConverter.convert(dto);
        if (businessEntity != null) {
            businessEntity.setUser(user);
            businessDetailRepository.save(businessEntity);
        }

        // Brand Detail
        BrandDetailEntity brandEntity = brandDetailConverter.convert(dto);
        if (brandEntity != null) {
            brandEntity.setUser(user);
            brandDetailRepository.save(brandEntity);
        }

        // Help Us Detail
        HelpUsEntity helpUsEntity = helpUsConverter.convert(dto);
        if (helpUsEntity != null) {
            helpUsEntity.setUser(user);
            helpUsRepository.save(helpUsEntity);
        }

        app.setCurrentStep(OnboardStep.SPORT_DETAILS);
        applicationRepository.save(app);
    }

    @Override
    @Transactional
    public void saveFacilityInfo(String userId, FacilitiesRequestDto dto) {
        UserEntity user = getUser(userId);
        OnboardingApplicationEntity app = getOnboardingApplication(user);

        // Clear existing facilities if needed, or simply append. For now, assuming new addition.
        if (dto.getFacilities() != null) {
            for (FacilityRequestDto facilityDto : dto.getFacilities()) {
                FacilityEntity facilityEntity = facilityConverter.convert(facilityDto);
                if (facilityEntity != null) {
                    facilityEntity.setUser(user);
                    // Initialize sports list
                    List<SportDetailEntity> sports = new ArrayList<>();
                    if (facilityDto.getSports() != null) {
                        for (SportDetailRequestDto sportDto : facilityDto.getSports()) {
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
    public void savePartnerDetails(String userId, PartnerDetailRequestDto dto) {
        UserEntity user = getUser(userId);
        OnboardingApplicationEntity app = getOnboardingApplication(user);

        PartnerDetailEntity entity = partnerDetailConverter.convert(dto);
        if (entity != null) {
            entity.setUser(user);
            partnerDetailRepository.save(entity);
        }

        app.setCurrentStep(OnboardStep.BANK_DETAILS);
        applicationRepository.save(app);
    }

    @Override
    @Transactional
    public void saveBankDetails(String userId, BankDetailRequestDto bankDetailRequestDto) {
        if (!bankDetailRequestDto.getAccountNumber().equals(bankDetailRequestDto.getConfirmAccountNumber())) {
            throw new InvalidRequestException("Account numbers do not match.");
        }
        UserEntity user = getUser(userId);
        OnboardingApplicationEntity app = getOnboardingApplication(user);

        BankDetailEntity entity = bankDetailConverter.convert(bankDetailRequestDto);
        if (entity != null) {
            entity.setUser(user);
            bankDetailRepository.save(entity);
        }

        app.setCurrentStep(OnboardStep.CONTRACT);
        applicationRepository.save(app);
    }

    @Override
    @Transactional
    public void signContract(String userId, ContractRequestDto contractDto, HttpServletRequest request) {
        if (contractDto.getSignatureType() == SignatureType.TYPED) {
            if (contractDto.getTypedSignatureName() == null || contractDto.getTypedSignatureName().isBlank()) {
                throw new InvalidRequestException("Signature name is required for TYPED signature.");
            }
        } else if (contractDto.getSignatureType() == SignatureType.UPLOADED) {
            if (contractDto.getUploadedSignatureUrl() == null || contractDto.getUploadedSignatureUrl().isBlank()) {
                throw new InvalidRequestException("Signature URL is required for UPLOADED signature.");
            }
        }
        UserEntity user = getUser(userId);
        OnboardingApplicationEntity app = getOnboardingApplication(user);

        OnboardingContractEntity entity = contractConverter.convert(contractDto);
        if (entity != null) {
            entity.setUser(user);
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
        if(app.getIsSubmitted()) {
            throw new InvalidRequestException("Application has already been submitted.");
        }
        validateApplicationCompleteness(user);
        app.setCurrentStep(OnboardStep.UNDER_REVIEW);
        app.setIsSubmitted(true);
        app.setOnboardApplicationStatus(OnboardApplicationStatus.UNDER_REVIEW);
        applicationRepository.save(app);
        log.info("Application for user {} submitted successfully.", userId);
    }

    private OnboardingApplicationEntity createApplication(UserEntity user) {
        OnboardingApplicationEntity app = new OnboardingApplicationEntity();
        app.setUser(user);
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
        return applicationRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Onboarding Application not found"));
    }

    private void validateApplicationCompleteness(UserEntity app) {
        List<String> missingModules = new ArrayList<>();
        if (!businessDetailRepository.existsByUser(app)) {
            missingModules.add("Business Details");
        }
        if (!facilityRepository.existsByUser(app)) {
            missingModules.add("Facility and Sports Info");
        }
        if (!partnerDetailRepository.existsByUser(app)) {
            missingModules.add("Partner Personal Details");
        }
        if (!bankDetailRepository.existsByUser(app)) {
            missingModules.add("Bank Details");
        }
        if (!contractRepository.existsByUser(app)) {
            missingModules.add("Contract Signature");
        }
        if (!missingModules.isEmpty()) {
            throw new IllegalStateException("Application incomplete. Missing: " + String.join(", ", missingModules));
        }
    }
}
