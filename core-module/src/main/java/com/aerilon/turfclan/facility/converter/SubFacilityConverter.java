package com.aerilon.turfclan.facility.converter;

import com.aerilon.turfclan.facility.dto.SubFacilityRequestDto;
import com.aerilon.turfclan.facility.entity.SubFacilityEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashSet;

@Component
public class SubFacilityConverter implements Converter<SubFacilityRequestDto, SubFacilityEntity> {

    @Override
    public SubFacilityEntity convert(SubFacilityRequestDto source) {
        SubFacilityEntity entity = new SubFacilityEntity();
        entity.setSportType(source.getSportType());
        entity.setSubType(source.getSubType());
        entity.setNumberOfUnits(source.getNumberOfUnits());
        entity.setMaxPlayersPerUnit(source.getMaxPlayersPerUnit());
        entity.setCurrency(source.getCurrency());
        entity.setPricePerHour(source.getPricePerHour());
        entity.setPricePerSession(source.getPricePerSession());
        entity.setOpenTime(source.getOpenTime());
        entity.setCloseTime(source.getCloseTime());
        entity.setSlotDurationMinutes(source.getSlotDurationMinutes());
        entity.setBufferDuration(source.getBufferDuration());
        entity.setPrimeTimeSurgePercentage(source.getPrimeTimeSurgePercentage());
        entity.setPrimeTimeWindows(source.getPrimeTimeWindows() != null ? new HashSet<>(source.getPrimeTimeWindows()) : new HashSet<>());
        entity.setAvailableDays(source.getAvailableDays() != null ? new HashSet<>(source.getAvailableDays()) : new HashSet<>());
        entity.setLength(source.getLength());
        entity.setWidth(source.getWidth());
        entity.setSurfaceType(source.getSurfaceType());
        entity.setAmenities(source.getAmenities() != null ? new HashSet<>(source.getAmenities()) : new HashSet<>());
        entity.setCreatedAt(LocalDateTime.now());
        return entity;
    }

    public SubFacilityRequestDto toDto(SubFacilityEntity entity) {
        if (entity == null) return null;

        SubFacilityRequestDto dto = new SubFacilityRequestDto();
        dto.setSportType(entity.getSportType());
        dto.setSubType(entity.getSubType());
        dto.setNumberOfUnits(entity.getNumberOfUnits());
        dto.setMaxPlayersPerUnit(entity.getMaxPlayersPerUnit());
        dto.setCurrency(entity.getCurrency());
        dto.setPricePerHour(entity.getPricePerHour());
        dto.setPricePerSession(entity.getPricePerSession());
        dto.setOpenTime(entity.getOpenTime());
        dto.setCloseTime(entity.getCloseTime());
        dto.setSlotDurationMinutes(entity.getSlotDurationMinutes());
        dto.setBufferDuration(entity.getBufferDuration());
        dto.setPrimeTimeSurgePercentage(entity.getPrimeTimeSurgePercentage());
        dto.setPrimeTimeWindows(entity.getPrimeTimeWindows() != null ?
                new HashSet<>(entity.getPrimeTimeWindows()) : new HashSet<>());
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
