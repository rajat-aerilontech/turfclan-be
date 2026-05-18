package com.aerilon.turfclan.partner.service.impl;

import com.aerilon.turfclan.dto.S3ImageResponseDto;
import com.aerilon.turfclan.facility.entity.FacilityEntity;
import com.aerilon.turfclan.partner.converter.PartnerFacilitySubFacilityConverter;
import com.aerilon.turfclan.partner.dto.DashboardDto;
import com.aerilon.turfclan.partner.dto.FacilityDto;
import com.aerilon.turfclan.partner.entity.BankDetailEntity;
import com.aerilon.turfclan.partner.entity.BrandDetailEntity;
import com.aerilon.turfclan.partner.entity.BusinessDetailEntity;
import com.aerilon.turfclan.partner.entity.PartnerDetailEntity;
import com.aerilon.turfclan.partner.repository.*;
import com.aerilon.turfclan.partner.service.PartnerService;
import com.aerilon.turfclan.service.S3Service;
import com.aerilon.turfclan.user.entity.UserEntity;
import com.aerilon.turfclan.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class PartnerServiceImpl implements PartnerService {

    private final UserRepository userRepository;
    private final BusinessDetailRepository businessDetailRepository;
    private final BrandDetailRepository brandDetailRepository;
    private final PartnerDetailRepository partnerDetailRepository;
    private final BankDetailRepository bankDetailRepository;
    private final FacilityRepository facilityRepository;
    private final PartnerFacilitySubFacilityConverter partnerFacilitySubFacilityConverter;
    private final S3Service s3Service;

    @Override
    public DashboardDto getFullDashboardData(String userId) {
        log.info("Fetching dashboard data for userId: {}", userId);
        UserEntity user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new RuntimeException("User not found"));

        BusinessDetailEntity business = businessDetailRepository.findByUser(user);
        PartnerDetailEntity partner = partnerDetailRepository.findByUser(user).orElse(null);
        BankDetailEntity bank = bankDetailRepository.findByUser(user).orElse(null);
        BrandDetailEntity brand = brandDetailRepository.findByUser(user);

        DashboardDto.DashboardDtoBuilder builder = DashboardDto.builder().partnerStatus(user.isVerified());

        if (business != null) {
            builder.businessName(business.getBusinessName())
                    .panNumber(business.getPanNumber())
                    .gstNumber(business.getGstNumber());
        }
        if (partner != null) {
            builder.partnerName(partner.getFullName())
                    .designation(partner.getDesignation())
                    .email(partner.getEmailId())
                    .mobile(partner.getPhonenumber());
            if (partner.getProfileImageUrl() != null) {
                String key = partner.getProfileImageUrl().getKey();
                S3ImageResponseDto profileImage = new S3ImageResponseDto(
                        key,
                        s3Service.preSignedUrl(key, 10)
                );
                builder.profileImageUrl(profileImage);
            }
        }
        if (bank != null) {
            builder.accountHolderName(bank.getAccountHolderName())
                    .accountNumber(maskAccountNumber(bank.getAccountNumber()))
                    .bankName(bank.getBankName())
                    .ifscCode(bank.getIfscCode());
        }
        if (brand != null) {
            builder.instagram(brand.getInstagramPage())
                    .youtube(brand.getYoutubePage())
                    .build();
        }
        return builder.build();
    }

    private String maskAccountNumber(String acc) {
        if (acc == null || acc.length() < 4) return "XXXXXXXXXX";
        return "XXXX-XX" + acc.substring(acc.length() - 4);
    }


    @Override
    public List<FacilityDto> getFullFacilityData(String userId) {
        log.info("Fetching facility data for userId: {}", userId);
        UserEntity user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<FacilityEntity> facilities = facilityRepository.findByUser(user);
        return facilities.stream()
                .map(partnerFacilitySubFacilityConverter::convert)
                .toList();
    }
}
