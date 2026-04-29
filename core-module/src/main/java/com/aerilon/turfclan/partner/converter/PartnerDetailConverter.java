package com.aerilon.turfclan.partner.converter;

import com.aerilon.turfclan.partner.dto.PartnerDetailRequestDto;
import com.aerilon.turfclan.partner.entity.PartnerDetailEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class PartnerDetailConverter implements Converter<PartnerDetailRequestDto, PartnerDetailEntity> {

    @Override
    public PartnerDetailEntity convert(PartnerDetailRequestDto source) {
        PartnerDetailEntity entity = new PartnerDetailEntity();
        entity.setFullName(source.getFullName());
        entity.setPhonenumber(source.getPhonenumber());
        entity.setEmailId(source.getEmail());
        entity.setDesignation(source.getDesignation());
        entity.setProfileImageUrl(source.getProfileImageUrl());
        entity.setAadharNumber(source.getAadharNumber());
        entity.setPanNumber(source.getPanNumber());
        entity.setIdProofType(source.getIdProofType());
        entity.setIdDocumentUrl(source.getIdDocumentUrl());
        entity.setCreatedAt(LocalDateTime.now());
        return entity;
    }

    public PartnerDetailRequestDto toDto(PartnerDetailEntity entity) {
        if (entity == null) return null;

        PartnerDetailRequestDto dto = new PartnerDetailRequestDto();
        dto.setFullName(entity.getFullName());
        dto.setPhonenumber(entity.getPhonenumber());
        dto.setEmail(entity.getEmailId());
        dto.setDesignation(entity.getDesignation());
        dto.setProfileImageUrl(entity.getProfileImageUrl());
        dto.setAadharNumber(entity.getAadharNumber());
        dto.setPanNumber(entity.getPanNumber());
        dto.setIdProofType(entity.getIdProofType());
        dto.setIdDocumentUrl(entity.getIdDocumentUrl());

        return dto;
    }
}
