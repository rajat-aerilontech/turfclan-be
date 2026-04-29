package com.aerilon.turfclan.partner.dto;

import com.aerilon.turfclan.partner.enums.OnboardApplicationStatus;
import com.aerilon.turfclan.partner.enums.OnboardStep;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OnboardingFullDataDto {
    private OnboardApplicationStatus status;
    private OnboardStep currentStep;
    private Boolean isSubmitted;

    private BusinessInfoDto businessInfo;
    private List<FacilityDto> facilities;
    private PartnerDetailDto partnerDetails;
    private BankDetailDto bankDetails;
    private ContractDto contractDetails;
}
