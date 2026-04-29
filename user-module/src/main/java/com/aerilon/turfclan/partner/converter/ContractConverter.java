package com.aerilon.turfclan.partner.converter;

import com.aerilon.turfclan.partner.dto.ContractDto;
import com.aerilon.turfclan.partner.entity.OnboardingContractEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class ContractConverter implements Converter<ContractDto, OnboardingContractEntity> {

    @Override
    public OnboardingContractEntity convert(ContractDto source) {
        OnboardingContractEntity entity = new OnboardingContractEntity();
        entity.setAgreementVersion(source.getAgreementVersion());
        entity.setIsAgreed(source.getIsAgreed());
        entity.setSignatureType(source.getSignatureType());
        entity.setTypedSignatureName(source.getTypedSignatureName());
        entity.setUploadedSignatureUrl(source.getUploadedSignatureUrl());
        return entity;
    }

    public ContractDto toDto(OnboardingContractEntity entity) {
        if (entity == null) return null;

        ContractDto dto = new ContractDto();
        dto.setAgreementVersion(entity.getAgreementVersion());
        dto.setIsAgreed(entity.getIsAgreed());
        dto.setSignatureType(entity.getSignatureType());
        dto.setTypedSignatureName(entity.getTypedSignatureName());
        dto.setUploadedSignatureUrl(entity.getUploadedSignatureUrl());

        return dto;
    }
}
