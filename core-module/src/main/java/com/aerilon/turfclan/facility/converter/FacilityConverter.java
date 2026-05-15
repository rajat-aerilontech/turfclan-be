package com.aerilon.turfclan.facility.converter;

import com.aerilon.turfclan.facility.dto.FacilityRequestDto;
import com.aerilon.turfclan.facility.entity.FacilityEntity;
import com.aerilon.turfclan.dto.S3ImageModelDto;
import com.aerilon.turfclan.service.S3Service;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class FacilityConverter implements Converter<FacilityRequestDto, FacilityEntity> {

    @Autowired
    private SubFacilityConverter subFacilityConverter;

    @Autowired
    private S3Service s3Service;

    @Override
    public FacilityEntity convert(FacilityRequestDto source) {
        FacilityEntity entity = new FacilityEntity();
        entity.setFacilityName(source.getFacilityName());
        entity.setDescription(source.getDescription());
//        if (source.getFacilityPhotos() != null) {
//            List<S3ImageModelDto> images = source.getFacilityPhotos().stream()
//                    .map(dto -> new S3ImageModelDto(dto.getKey())) // ignore URL
//                    .toList();
//            entity.setFacilityPhotos(images);
//        } else {
//            entity.setFacilityPhotos(new ArrayList<>());
//        }
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
        entity.setCanBeBooked(source.getCanBeBooked());
        entity.setCreatedAt(LocalDateTime.now());
        return entity;
    }

    public FacilityRequestDto toDto(FacilityEntity entity) {
        if (entity == null) return null;

        FacilityRequestDto dto = new FacilityRequestDto();
        dto.setFacilityName(entity.getFacilityName());
        dto.setDescription(entity.getDescription());
//        if (entity.getFacilityPhotos() != null) {
//            List<com.aerilon.turfclan.dto.S3ImageResponseDto> images = entity.getFacilityPhotos().stream()
//                    .map(img -> new com.aerilon.turfclan.dto.S3ImageResponseDto(
//                            img.getKey(),
//                            s3Service.preSignedUrl(img.getKey(), 10)
//                    ))
//                    .toList();
//            dto.setFacilityPhotos(images);
//        }
        dto.setAddressLine1(entity.getAddressLine1());
        dto.setAddressLine2(entity.getAddressLine2());
        dto.setLandmark(entity.getLandmark());
        dto.setPincode(entity.getPincode());
        dto.setCity(entity.getCity());
        dto.setState(entity.getState());
        dto.setCanBeBooked(entity.getCanBeBooked());
        if (entity.getLocation() != null) {
            dto.setLongitude(entity.getLocation().getX());
            dto.setLatitude(entity.getLocation().getY());
        }
        if (entity.getSubFacility() != null) {
            dto.setSubFacilities(entity.getSubFacility().stream()
                    .map(subFacilityConverter::toDto)
                    .collect(Collectors.toList()));
        }
        return dto;
    }
}
