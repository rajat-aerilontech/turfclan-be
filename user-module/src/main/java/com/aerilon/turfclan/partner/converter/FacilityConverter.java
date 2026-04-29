package com.aerilon.turfclan.partner.converter;

import com.aerilon.turfclan.partner.dto.FacilityRequestDto;
import com.aerilon.turfclan.partner.entity.FacilityEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Component
public class FacilityConverter implements Converter<FacilityRequestDto, FacilityEntity> {

    @Autowired
    private SportDetailConverter sportDetailConverter;

    @Override
    public FacilityEntity convert(FacilityRequestDto source) {
        FacilityEntity entity = new FacilityEntity();
        entity.setFacilityName(source.getFacilityName());
        entity.setDescription(source.getDescription());
        entity.setFacilityPhotos(source.getFacilityPhotos() != null ? source.getFacilityPhotos() : new ArrayList<>());
        entity.setCreatedAt(LocalDateTime.now());
        return entity;
    }

    public FacilityRequestDto toDto(FacilityEntity entity) {
        if (entity == null) return null;

        FacilityRequestDto dto = new FacilityRequestDto();
        dto.setFacilityName(entity.getFacilityName());
        dto.setDescription(entity.getDescription());
        dto.setFacilityPhotos(entity.getFacilityPhotos());
        if (entity.getSports() != null) {
            dto.setSports(entity.getSports().stream()
                    .map(sportDetailConverter::toDto)
                    .collect(Collectors.toList()));
        }
        return dto;
    }
}
