package com.aerilon.turfclan.partner.converter;

import com.aerilon.turfclan.partner.dto.ContractRequestDto;
import com.aerilon.turfclan.partner.entity.OnboardingContractEntity;
import com.aerilon.turfclan.service.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class ContractConverter implements Converter<ContractRequestDto, OnboardingContractEntity> {

    @Autowired
    private S3Service s3Service;

    @Override
    public OnboardingContractEntity convert(ContractRequestDto source) {
        OnboardingContractEntity entity = new OnboardingContractEntity();
        entity.setAgreementVersion(source.getAgreementVersion());
        entity.setIsAgreed(source.getIsAgreed());
        entity.setSignatureType(source.getSignatureType());
        entity.setTypedSignatureName(source.getTypedSignatureName());
        return entity;
    }

    public ContractRequestDto toDto(OnboardingContractEntity entity) {
        if (entity == null) return null;

        ContractRequestDto dto = new ContractRequestDto();
        dto.setAgreementVersion(entity.getAgreementVersion());
        dto.setIsAgreed(entity.getIsAgreed());
        dto.setSignatureType(entity.getSignatureType());
        dto.setTypedSignatureName(entity.getTypedSignatureName());
        if (entity.getUploadedSignatureUrl() != null && !entity.getUploadedSignatureUrl().isBlank()) {
            dto.setUploadedSignatureUrl(s3Service.preSignedUrl(entity.getUploadedSignatureUrl(), 10));
        }
        return dto;
    }
}
