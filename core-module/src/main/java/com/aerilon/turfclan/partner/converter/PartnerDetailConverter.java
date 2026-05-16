package com.aerilon.turfclan.partner.converter;

import com.aerilon.turfclan.dto.S3ImageModelDto;
import com.aerilon.turfclan.dto.S3ImageResponseDto;
import com.aerilon.turfclan.partner.dto.PartnerDetailRequestDto;
import com.aerilon.turfclan.partner.entity.PartnerDetailEntity;
import com.aerilon.turfclan.service.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class PartnerDetailConverter implements Converter<PartnerDetailRequestDto, PartnerDetailEntity> {

    @Autowired
    private S3Service s3Service;

    @Override
    public PartnerDetailEntity convert(PartnerDetailRequestDto source) {
        PartnerDetailEntity entity = new PartnerDetailEntity();
        entity.setFullName(source.getFullName());
        entity.setPhonenumber(source.getPhonenumber());
        entity.setEmailId(source.getEmail());
        entity.setDesignation(source.getDesignation());
        entity.setAadharNumber(source.getAadharNumber());
        entity.setPanNumber(source.getPanNumber());
        entity.setIdProofType(source.getIdProofType());
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
        dto.setAadharNumber(entity.getAadharNumber());
        dto.setPanNumber(entity.getPanNumber());
        dto.setIdProofType(entity.getIdProofType());

        return dto;
    }
}
