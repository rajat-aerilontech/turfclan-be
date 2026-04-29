package com.aerilon.turfclan.facility.converter;

import com.aerilon.turfclan.facility.dto.FacilityRequestDto;
import com.aerilon.turfclan.facility.entity.FacilityEntity;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
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
        entity.setAddressLine1(source.getAddressLine1());
        entity.setAddressLine2(source.getAddressLine2());
        entity.setLandmark(source.getLandmark());
        entity.setPincode(source.getPincode());
        entity.setCity(source.getCity());
        entity.setState(source.getState());
        // Handle Location Conversion
        Double lat = source.getLatitude();
        Double lon = source.getLongitude();
        if (lat != null && lon != null) {
            // Coordinate order is (X, Y) which is (Longitude, Latitude)
            GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
            Point point = geometryFactory.createPoint(new Coordinate(lon, lat));
            entity.setLocation(point);
        }
        entity.setCreatedAt(LocalDateTime.now());
        return entity;
    }

    public FacilityRequestDto toDto(FacilityEntity entity) {
        if (entity == null) return null;

        FacilityRequestDto dto = new FacilityRequestDto();
        dto.setFacilityName(entity.getFacilityName());
        dto.setDescription(entity.getDescription());
        dto.setFacilityPhotos(entity.getFacilityPhotos());
        dto.setAddressLine1(entity.getAddressLine1());
        dto.setAddressLine2(entity.getAddressLine2());
        dto.setLandmark(entity.getLandmark());
        dto.setPincode(entity.getPincode());
        dto.setCity(entity.getCity());
        dto.setState(entity.getState());
        if (entity.getLocation() != null) {
            dto.setLongitude(entity.getLocation().getX());
            dto.setLatitude(entity.getLocation().getY());
        }
        if (entity.getSports() != null) {
            dto.setSports(entity.getSports().stream()
                    .map(sportDetailConverter::toDto)
                    .collect(Collectors.toList()));
        }
        return dto;
    }
}
