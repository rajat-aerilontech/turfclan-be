package com.aerilon.turfclan.partner.service.impl;

import com.aerilon.turfclan.partner.dto.DashboardDto;
import com.aerilon.turfclan.partner.entity.BankDetailEntity;
import com.aerilon.turfclan.partner.entity.BrandDetailEntity;
import com.aerilon.turfclan.partner.entity.BusinessDetailEntity;
import com.aerilon.turfclan.partner.entity.PartnerDetailEntity;
import com.aerilon.turfclan.partner.repository.BankDetailRepository;
import com.aerilon.turfclan.partner.repository.BrandDetailRepository;
import com.aerilon.turfclan.partner.repository.BusinessDetailRepository;
import com.aerilon.turfclan.partner.repository.PartnerDetailRepository;
import com.aerilon.turfclan.partner.service.PartnerService;
import com.aerilon.turfclan.user.entity.UserEntity;
import com.aerilon.turfclan.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
}
