package com.aerilon.turfclan.partner.converter;

import com.aerilon.turfclan.partner.dto.SportDetailDto;
import com.aerilon.turfclan.partner.entity.SportDetailEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;

@Component
public class SportDetailConverter implements Converter<SportDetailDto, SportDetailEntity> {

    @Override
    public SportDetailEntity convert(SportDetailDto source) {
        SportDetailEntity entity = new SportDetailEntity();
        entity.setSportType(source.getSportType());
        entity.setSubType(source.getSubType());
        entity.setNumberOfUnits(source.getNumberOfUnits());
        entity.setMaxPlayersPerUnit(source.getMaxPlayersPerUnit());
        entity.setCurrency(source.getCurrency());
        entity.setPricePerHour(source.getPricePerHour());
        entity.setPricePerSession(source.getPricePerSession());
        entity.setOpenTime(source.getOpenTime());
        entity.setCloseTime(source.getCloseTime());
        entity.setAvailableDays(source.getAvailableDays() != null ? new HashSet<>(source.getAvailableDays()) : new HashSet<>());
        entity.setLength(source.getLength());
        entity.setWidth(source.getWidth());
        entity.setSurfaceType(source.getSurfaceType());
        entity.setAmenities(source.getAmenities() != null ? new HashSet<>(source.getAmenities()) : new HashSet<>());
        entity.setCreatedAt(LocalDateTime.now());
        return entity;
    }

    public SportDetailDto toDto(SportDetailEntity entity) {
        if (entity == null) return null;

        SportDetailDto dto = new SportDetailDto();
        dto.setSportType(entity.getSportType());
        dto.setSubType(entity.getSubType());
        dto.setNumberOfUnits(entity.getNumberOfUnits());
        dto.setMaxPlayersPerUnit(entity.getMaxPlayersPerUnit());
        dto.setCurrency(entity.getCurrency());
        dto.setPricePerHour(entity.getPricePerHour());
        dto.setPricePerSession(entity.getPricePerSession());
        dto.setOpenTime(entity.getOpenTime());
        dto.setCloseTime(entity.getCloseTime());
        dto.setAvailableDays(entity.getAvailableDays() != null ?
                new HashSet<>(entity.getAvailableDays()) : new HashSet<>());
        dto.setLength(entity.getLength());
        dto.setWidth(entity.getWidth());
        dto.setSurfaceType(entity.getSurfaceType());
        dto.setAmenities(entity.getAmenities() != null ?
                new HashSet<>(entity.getAmenities()) : new HashSet<>());
        return dto;
    }
}
