package com.aerilon.turfclan.partner.service;

import com.aerilon.turfclan.partner.dto.*;
import com.aerilon.turfclan.partner.enums.OnboardStep;
import jakarta.servlet.http.HttpServletRequest;

public interface OnboardingService {
    OnboardStep getCurrentOnboardingStep(String userId);
    void saveBusinessInfo(String userId, BusinessInfoDto dto);
    void saveFacilityInfo(String userId, FacilitiesDto dto);
    void savePartnerDetails(String userId, PartnerDetailDto dto);
    void saveBankDetails(String userId, BankDetailDto dto);
    void signContract(String userId, ContractDto dto, HttpServletRequest request);
    void submitApplication(String userId);
    OnboardingFullDataDto getFullOnboardingData(String userId);
}
