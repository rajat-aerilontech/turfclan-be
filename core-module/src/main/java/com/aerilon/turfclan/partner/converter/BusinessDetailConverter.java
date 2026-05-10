package com.aerilon.turfclan.partner.converter;

import com.aerilon.turfclan.partner.dto.BusinessDetailRequestDto;
import com.aerilon.turfclan.partner.dto.BusinessInfoDto;
import com.aerilon.turfclan.partner.entity.BusinessDetailEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class BusinessDetailConverter implements Converter<BusinessInfoDto, BusinessDetailEntity> {

    @Override
    public BusinessDetailEntity convert(BusinessInfoDto source) {
        if (source.getBusinessDetail() == null) {
            return null;
        }
        BusinessDetailRequestDto detail = source.getBusinessDetail();
        BusinessDetailEntity entity = new BusinessDetailEntity();
        entity.setBusinessName(detail.getBusinessName());
        entity.setBusinessType(detail.getBusinessType());
        entity.setGstNumber(detail.getGstNumber());
        entity.setPanNumber(detail.getPanNumber());
        entity.setAddressLine1(detail.getAddressLine1());
        entity.setAddressLine2(detail.getAddressLine2());
        entity.setLandmark(detail.getLandmark());
        entity.setPincode(detail.getPincode());
        entity.setCity(detail.getCity());
        entity.setState(detail.getState());
        entity.setCreatedAt(LocalDateTime.now());
        return entity;
    }

    public BusinessDetailRequestDto toDto(BusinessDetailEntity entity) {
        if (entity == null) return null;
        BusinessDetailRequestDto dto = new BusinessDetailRequestDto();
        dto.setBusinessName(entity.getBusinessName());
        dto.setBusinessType(entity.getBusinessType());
        dto.setGstNumber(entity.getGstNumber());
        dto.setPanNumber(entity.getPanNumber());
        dto.setAddressLine1(entity.getAddressLine1());
        dto.setAddressLine2(entity.getAddressLine2());
        dto.setLandmark(entity.getLandmark());
        dto.setPincode(entity.getPincode());
        dto.setCity(entity.getCity());
        dto.setState(entity.getState());
        return dto;
    }
}
