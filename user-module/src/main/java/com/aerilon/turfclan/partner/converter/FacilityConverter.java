package com.aerilon.turfclan.partner.converter;

import com.aerilon.turfclan.partner.dto.FacilityDto;
import com.aerilon.turfclan.partner.entity.FacilityEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Component
public class FacilityConverter implements Converter<FacilityDto, FacilityEntity> {

    @Override
    public FacilityEntity convert(FacilityDto source) {
        FacilityEntity entity = new FacilityEntity();
        entity.setFacilityName(source.getFacilityName());
        entity.setDescription(source.getDescription());
        entity.setFacilityPhotos(source.getFacilityPhotos() != null ? source.getFacilityPhotos() : new ArrayList<>());
        entity.setCreatedAt(LocalDateTime.now());
        return entity;
    }
}
