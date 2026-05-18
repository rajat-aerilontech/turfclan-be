package com.aerilon.turfclan.partner.converter;

import com.aerilon.turfclan.dto.S3ImageResponseDto;
import com.aerilon.turfclan.facility.entity.FacilityEntity;
import com.aerilon.turfclan.facility.entity.SubFacilityEntity;
import com.aerilon.turfclan.partner.dto.FacilityDto;
import com.aerilon.turfclan.partner.dto.SubFacilityDto;
import com.aerilon.turfclan.service.S3Service;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component("partnerFacilityConverter")
public class PartnerFacilitySubFacilityConverter implements Converter<FacilityEntity, FacilityDto> {

    @Autowired
    private S3Service s3Service;

    @Override
    public FacilityDto convert(FacilityEntity entity) {

        if (entity == null) {
            return null;
        }

        Point location = entity.getLocation();

        return FacilityDto.builder()
                .facilityId(entity.getId() != null ? entity.getId().toString() : null)
                .facilityName(entity.getFacilityName())
                .description(entity.getDescription())
                .facilityPhotos(convertPhotos(entity))
                .addressLine1(entity.getAddressLine1())
                .addressLine2(entity.getAddressLine2())
                .landmark(entity.getLandmark())
                .pincode(entity.getPincode())
                .city(entity.getCity())
                .state(entity.getState())
                .canBeBooked(entity.getCanBeBooked())
                .latitude(location != null ? location.getY() : null)
                .longitude(location != null ? location.getX() : null)
                .subFacilities(convertSubFacilities(entity))
                .build();
    }

    private List<S3ImageResponseDto> convertPhotos(FacilityEntity entity) {
        if (entity.getFacilityPhotos() == null) {
            return List.of();
        }

        return entity.getFacilityPhotos().stream()
                .filter(Objects::nonNull)
                .map(img -> new S3ImageResponseDto(
                        img.getKey(),
                        s3Service.preSignedUrl(img.getKey(), 10)
                ))
                .toList();
    }

    private List<SubFacilityDto> convertSubFacilities(FacilityEntity entity) {
        if (entity.getSubFacility() == null) {
            return List.of();
        }

        return entity.getSubFacility().stream()
                .map(this::convertSubFacility)
                .toList();
    }

    private SubFacilityDto convertSubFacility(SubFacilityEntity entity) {
        if (entity == null) {
            return null;
        }

        return SubFacilityDto.builder()
                .id(entity.getId() != null ? entity.getId().toString() : null)
                .sportType(entity.getSportType() != null ? entity.getSportType().name() : null)
                .subType(entity.getSubType())
                .numberOfUnits(entity.getNumberOfUnits())
                .maxPlayersPerUnit(entity.getMaxPlayersPerUnit())
                .currency(entity.getCurrency())
                .pricePerHour(entity.getPricePerHour())
                .pricePerSession(entity.getPricePerSession())
                .openTime(entity.getOpenTime() != null ? entity.getOpenTime().toString() : null)
                .closeTime(entity.getCloseTime() != null ? entity.getCloseTime().toString() : null)
                .slotDurationMinutes(entity.getSlotDurationMinutes())
                .bufferDuration(entity.getBufferDuration())
                .primeTimeSurgePercentage(entity.getPrimeTimeSurgePercentage())
                .primeTimeWindows(entity.getPrimeTimeWindows())
                .availableDays(entity.getAvailableDays())
                .length(entity.getLength())
                .width(entity.getWidth())
                .surfaceType(entity.getSurfaceType())
                .amenities(entity.getAmenities())
                .build();
    }
}
