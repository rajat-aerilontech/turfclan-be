package com.aerilon.turfclan.partner.service;

import com.aerilon.turfclan.facility.dto.FacilitiesRequestDto;
import com.aerilon.turfclan.partner.dto.*;
import com.aerilon.turfclan.partner.enums.OnboardStep;
import jakarta.servlet.http.HttpServletRequest;

public interface OnboardingService {
    OnboardStep getCurrentOnboardingStep(String userId);
    void saveBusinessInfo(String userId, BusinessInfoDto dto);
    void saveFacilityInfo(String userId, FacilitiesRequestDto dto);
    void savePartnerDetails(String userId, PartnerDetailRequestDto dto);
    void saveBankDetails(String userId, BankDetailRequestDto dto);
    void signContract(String userId, ContractRequestDto dto, HttpServletRequest request);
    void submitApplication(String userId);
    OnboardingFullDataDto getFullOnboardingData(String userId);
}
