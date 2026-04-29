package com.aerilon.turfclan.facility.service.impl;

import com.aerilon.turfclan.exception.ResourceNotFoundException;
import com.aerilon.turfclan.exception.UnauthorizedAccessException;
import com.aerilon.turfclan.facility.service.FacilityService;
import com.aerilon.turfclan.facility.dto.FacilitiesMobileResponseDto;
import com.aerilon.turfclan.facility.dto.FacilityMobileResponseDto;
import com.aerilon.turfclan.facility.dto.FacilityUpdateDto;
import com.aerilon.turfclan.facility.dto.SportDetailUpdateDto;
import com.aerilon.turfclan.partner.converter.FacilityConverter;
import com.aerilon.turfclan.partner.converter.SportDetailConverter;
import com.aerilon.turfclan.partner.dto.FacilitiesRequestDto;
import com.aerilon.turfclan.partner.dto.FacilityRequestDto;
import com.aerilon.turfclan.partner.dto.SportDetailRequestDto;
import com.aerilon.turfclan.partner.entity.FacilityEntity;
import com.aerilon.turfclan.partner.entity.SportDetailEntity;
import com.aerilon.turfclan.partner.repository.FacilityRepository;
import com.aerilon.turfclan.partner.repository.SportDetailRepository;
import com.aerilon.turfclan.user.entity.UserEntity;
import com.aerilon.turfclan.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class FacilityServiceImpl implements FacilityService {

    private final FacilityRepository facilityRepository;
    private final UserRepository userRepository;
    private final FacilityConverter facilityConverter;
    private final SportDetailRepository sportDetailRepository;
    private final SportDetailConverter sportDetailConverter;

    @Override
    public FacilitiesRequestDto getFacilityForUser(String userId) {
        log.info("Fetching facility details for user: {}", userId);

        UserEntity user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new RuntimeException("User not found"));

        FacilitiesRequestDto dto = new FacilitiesRequestDto();
        dto.setFacilities(facilityRepository.findByUser(user).stream()
                .map(facilityConverter::toDto)
                .toList());

        if (dto.getFacilities().isEmpty()) {
            log.warn("No facility found for user: {}", userId);
        }

        return dto;
    }

    @Override
    public FacilitiesRequestDto getAllFacility() {
        log.info("Fetching all facilities");

        FacilitiesRequestDto dto = new FacilitiesRequestDto();
        dto.setFacilities(facilityRepository.findAll().stream()
                .map(facilityConverter::toDto)
                .toList());

        log.info("Found {} facilities", dto.getFacilities().size());
        return dto;
    }

    @Override
    public FacilitiesMobileResponseDto getAllFacilitiesForMobile(Double userLatitude, Double userLongitude,
                                                                   String sortBy, Double maxDistanceKm) {
        log.info("Fetching facilities for mobile user at location: ({}, {}), sortBy: {}, maxDistance: {} km",
                userLatitude, userLongitude, sortBy, maxDistanceKm);

        // Default values
        if (maxDistanceKm == null || maxDistanceKm <= 0) {
            maxDistanceKm = 50.0; // Default 50 km
        }
        if (sortBy == null || sortBy.isBlank()) {
            sortBy = "distance_price"; // Default sorting
        }

        Double maxDistanceMeters = maxDistanceKm * 1000;

        // Fetch facilities within the max distance
        List<FacilityEntity> facilities = facilityRepository.findFacilitiesByDistance(
                userLongitude, userLatitude, maxDistanceMeters);

        log.info("Found {} facilities within {} km", facilities.size(), maxDistanceKm);

        // Convert to mobile response DTOs with distance and price information
        List<FacilityMobileResponseDto> mobileFacilities = facilities.stream()
                .map(facility -> convertToMobileResponseDto(facility, userLatitude, userLongitude))
                .collect(Collectors.toList());

        // Apply sorting based on sortBy parameter
        mobileFacilities = sortFacilities(mobileFacilities, sortBy);

        return FacilitiesMobileResponseDto.builder()
                .facilities(mobileFacilities)
                .totalCount(mobileFacilities.size())
                .build();
    }

    /**
     * Convert FacilityEntity to FacilityMobileResponseDto with distance and lowest price
     */
    private FacilityMobileResponseDto convertToMobileResponseDto(FacilityEntity facility,
                                                                   Double userLatitude, Double userLongitude) {
        // Calculate distance
        Double distanceKm = calculateDistance(facility.getLocation(), userLatitude, userLongitude);

        // Get lowest price among all sports
        BigDecimal lowestPrice = BigDecimal.valueOf(Double.MAX_VALUE);
        String lowestPriceCurrency = "INR"; // Default currency

        if (facility.getSports() != null && !facility.getSports().isEmpty()) {
            SportDetailEntity firstSport = facility.getSports().get(0);
            lowestPriceCurrency = firstSport.getCurrency();

            lowestPrice = facility.getSports().stream()
                    .map(SportDetailEntity::getPricePerHour)
                    .min(BigDecimal::compareTo)
                    .orElse(BigDecimal.ZERO);
        }

        return FacilityMobileResponseDto.builder()
                .id(facility.getId())
                .facilityName(facility.getFacilityName())
                .description(facility.getDescription())
                .facilityPhotos(facility.getFacilityPhotos())
                .addressLine1(facility.getAddressLine1())
                .addressLine2(facility.getAddressLine2())
                .landmark(facility.getLandmark())
                .pincode(facility.getPincode())
                .city(facility.getCity())
                .state(facility.getState())
                .latitude(facility.getLocation() != null ? facility.getLocation().getY() : null)
                .longitude(facility.getLocation() != null ? facility.getLocation().getX() : null)
                .distanceKm(distanceKm)
                .lowestPrice(lowestPrice)
                .lowestPriceCurrency(lowestPriceCurrency)
                .sports(facility.getSports().stream()
                        .map(this::convertSportDetail)
                        .collect(Collectors.toList()))
                .build();
    }

    /**
     * Sort facilities based on the sortBy parameter
     */
    private List<FacilityMobileResponseDto> sortFacilities(List<FacilityMobileResponseDto> facilities,
                                                            String sortBy) {
        return switch (sortBy.toLowerCase()) {
            case "distance" ->
                facilities.stream()
                        .sorted(Comparator.comparing(FacilityMobileResponseDto::getDistanceKm))
                        .collect(Collectors.toList());

            case "price" ->
                facilities.stream()
                        .sorted(Comparator.comparing(FacilityMobileResponseDto::getLowestPrice))
                        .collect(Collectors.toList());

            case "distance_price" ->
                // Sort by distance first, then by price
                facilities.stream()
                        .sorted(Comparator.comparing(FacilityMobileResponseDto::getDistanceKm)
                                .thenComparing(FacilityMobileResponseDto::getLowestPrice))
                        .collect(Collectors.toList());

            default -> {
                log.warn("Unknown sortBy value: {}. Using default distance_price sorting", sortBy);
                yield facilities.stream()
                        .sorted(Comparator.comparing(FacilityMobileResponseDto::getDistanceKm)
                                .thenComparing(FacilityMobileResponseDto::getLowestPrice))
                        .collect(Collectors.toList());
            }
        };
    }

    /**
     * Calculate distance between two geographical points using Haversine formula
     * Returns distance in kilometers
     */
    private Double calculateDistance(Point location, Double userLatitude, Double userLongitude) {
        if (location == null) {
            return Double.MAX_VALUE;
        }

        Double facilityLatitude = location.getY();
        Double facilityLongitude = location.getX();

        final int EARTH_RADIUS_KM = 6371; // Earth's radius in kilometers

        Double dLat = Math.toRadians(facilityLatitude - userLatitude);
        Double dLon = Math.toRadians(facilityLongitude - userLongitude);

        Double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                   Math.cos(Math.toRadians(userLatitude)) * Math.cos(Math.toRadians(facilityLatitude)) *
                   Math.sin(dLon / 2) * Math.sin(dLon / 2);

        Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        Double distance = EARTH_RADIUS_KM * c;

        return Math.round(distance * 100.0) / 100.0; // Round to 2 decimal places
    }

    /**
     * Convert SportDetailEntity to SportDetailRequestDto (for mobile response)
     */
    private com.aerilon.turfclan.partner.dto.SportDetailRequestDto convertSportDetail(SportDetailEntity sport) {
        com.aerilon.turfclan.partner.dto.SportDetailRequestDto dto = new com.aerilon.turfclan.partner.dto.SportDetailRequestDto();
        dto.setSportType(sport.getSportType());
        dto.setSubType(sport.getSubType());
        dto.setNumberOfUnits(sport.getNumberOfUnits());
        dto.setMaxPlayersPerUnit(sport.getMaxPlayersPerUnit());
        dto.setCurrency(sport.getCurrency());
        dto.setPricePerHour(sport.getPricePerHour());
        dto.setPricePerSession(sport.getPricePerSession());
        dto.setOpenTime(sport.getOpenTime());
        dto.setCloseTime(sport.getCloseTime());
        dto.setAvailableDays(sport.getAvailableDays());
        dto.setLength(sport.getLength());
        dto.setWidth(sport.getWidth());
        dto.setSurfaceType(sport.getSurfaceType());
        dto.setAmenities(sport.getAmenities());
        return dto;
    }

    @Override
    public FacilityRequestDto updateFacility(String userId, UUID facilityId, FacilityUpdateDto updateDto) {
        log.info("Updating facility: {} for user: {}", facilityId, userId);

        UserEntity user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        FacilityEntity facility = facilityRepository.findById(facilityId)
                .orElseThrow(() -> new ResourceNotFoundException("Facility not found"));

        if (!facility.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedAccessException("Unauthorized: Facility does not belong to this user");
        }

        if (updateDto.getFacilityName() != null && !updateDto.getFacilityName().isBlank()) {
            facility.setFacilityName(updateDto.getFacilityName());
        }
        if (updateDto.getDescription() != null && !updateDto.getDescription().isBlank()) {
            facility.setDescription(updateDto.getDescription());
        }
        if (updateDto.getFacilityPhotos() != null) {
            facility.setFacilityPhotos(updateDto.getFacilityPhotos());
        }
        if (updateDto.getAddressLine1() != null && !updateDto.getAddressLine1().isBlank()) {
            facility.setAddressLine1(updateDto.getAddressLine1());
        }
        if (updateDto.getAddressLine2() != null) {
            facility.setAddressLine2(updateDto.getAddressLine2());
        }
        if (updateDto.getLandmark() != null && !updateDto.getLandmark().isBlank()) {
            facility.setLandmark(updateDto.getLandmark());
        }
        if (updateDto.getPincode() != null && !updateDto.getPincode().isBlank()) {
            facility.setPincode(updateDto.getPincode());
        }
        if (updateDto.getCity() != null && !updateDto.getCity().isBlank()) {
            facility.setCity(updateDto.getCity());
        }
        if (updateDto.getState() != null && !updateDto.getState().isBlank()) {
            facility.setState(updateDto.getState());
        }

        if (updateDto.getLatitude() != null && updateDto.getLongitude() != null) {
            org.locationtech.jts.geom.GeometryFactory geometryFactory =
                new org.locationtech.jts.geom.GeometryFactory(
                    new org.locationtech.jts.geom.PrecisionModel(), 4326);
            Point point = geometryFactory.createPoint(
                new org.locationtech.jts.geom.Coordinate(updateDto.getLongitude(), updateDto.getLatitude()));
            facility.setLocation(point);
        }

        facilityRepository.save(facility);
        log.info("Facility updated successfully: {}", facilityId);

        return facilityConverter.toDto(facility);
    }

    @Override
    public FacilityRequestDto updateSportDetail(String userId, UUID facilityId, UUID sportId, SportDetailUpdateDto updateDto) {
        log.info("Updating sport detail for facility: {} by user: {}", facilityId, userId);

        UserEntity user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        FacilityEntity facility = facilityRepository.findById(facilityId)
                .orElseThrow(() -> new ResourceNotFoundException("Facility not found"));

        if (!facility.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedAccessException("Unauthorized: Facility does not belong to this user");
        }

        SportDetailEntity sportDetail = sportDetailRepository.findById(sportId)
                .orElseThrow(() -> new ResourceNotFoundException("Sport detail not found"));

        if (!sportDetail.getFacility().getId().equals(facilityId)) {
            throw new ResourceNotFoundException("Sport detail does not belong to this facility");
        }

        if (updateDto.getSubType() != null && !updateDto.getSubType().isBlank()) {
            sportDetail.setSubType(updateDto.getSubType());
        }
        if (updateDto.getNumberOfUnits() != null) {
            sportDetail.setNumberOfUnits(updateDto.getNumberOfUnits());
        }
        if (updateDto.getMaxPlayersPerUnit() != null) {
            sportDetail.setMaxPlayersPerUnit(updateDto.getMaxPlayersPerUnit());
        }
        if (updateDto.getCurrency() != null && !updateDto.getCurrency().isBlank()) {
            sportDetail.setCurrency(updateDto.getCurrency());
        }
        if (updateDto.getPricePerHour() != null) {
            sportDetail.setPricePerHour(updateDto.getPricePerHour());
        }
        if (updateDto.getPricePerSession() != null) {
            sportDetail.setPricePerSession(updateDto.getPricePerSession());
        }
        if (updateDto.getOpenTime() != null) {
            sportDetail.setOpenTime(updateDto.getOpenTime());
        }
        if (updateDto.getCloseTime() != null) {
            sportDetail.setCloseTime(updateDto.getCloseTime());
        }
        if (updateDto.getAvailableDays() != null && !updateDto.getAvailableDays().isEmpty()) {
            sportDetail.setAvailableDays(updateDto.getAvailableDays());
        }
        if (updateDto.getLength() != null) {
            sportDetail.setLength(updateDto.getLength());
        }
        if (updateDto.getWidth() != null) {
            sportDetail.setWidth(updateDto.getWidth());
        }
        if (updateDto.getSurfaceType() != null && !updateDto.getSurfaceType().isBlank()) {
            sportDetail.setSurfaceType(updateDto.getSurfaceType());
        }
        if (updateDto.getAmenities() != null) {
            sportDetail.setAmenities(updateDto.getAmenities());
        }

        sportDetailRepository.save(sportDetail);
        log.info("Sport detail updated successfully: {}", updateDto.getSportDetailId());

        return facilityConverter.toDto(facility);
    }

    @Override
    public FacilityRequestDto addSportDetailToFacility(String userId, UUID facilityId, SportDetailRequestDto sportDetailRequestDto) {
        log.info("Creating sport detail for facility: {} by user: {}", facilityId, userId);
        UserEntity user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        FacilityEntity facility = facilityRepository.findById(facilityId)
                .orElseThrow(() -> new ResourceNotFoundException("Facility not found"));
        SportDetailEntity sportDetailEntity = sportDetailConverter.convert(sportDetailRequestDto);

        if (sportDetailEntity != null) {
            sportDetailEntity.setFacility(facility);
            facility.getSports().add(sportDetailEntity);
            sportDetailRepository.save(sportDetailEntity);
        }
        return facilityConverter.toDto(facility);
    }

    @Override
    public FacilityRequestDto addFacilityForUser(String userId, FacilityRequestDto facilityRequestDto) {
        log.info("Add Facility by user: {}", userId);
        UserEntity user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        FacilityEntity facilityEntity = facilityConverter.convert(facilityRequestDto);
        if (facilityEntity != null) {
            facilityEntity.setUser(user);
            List<SportDetailEntity> sports = new ArrayList<>();
            if (facilityRequestDto.getSports() != null) {
                for (SportDetailRequestDto sportDto : facilityRequestDto.getSports()) {
                    SportDetailEntity sportEntity = sportDetailConverter.convert(sportDto);
                    if (sportEntity != null) {
                        sportEntity.setFacility(facilityEntity);
                        sports.add(sportEntity);
                    }
                }
            }
            facilityEntity.setSports(sports);
            FacilityEntity savedFacility = facilityRepository.save(facilityEntity);
            return facilityConverter.toDto(savedFacility);
        }
        throw new RuntimeException("Failed to create facility");
    }
}
