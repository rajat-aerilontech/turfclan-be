package com.aerilon.turfclan.partner.converter;

import com.aerilon.turfclan.partner.dto.SportDetailDto;
import com.aerilon.turfclan.partner.entity.SportDetailEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
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
}
