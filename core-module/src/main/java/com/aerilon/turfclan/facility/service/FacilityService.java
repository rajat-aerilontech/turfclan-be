package com.aerilon.turfclan.facility.service;

import com.aerilon.turfclan.partner.dto.FacilitiesRequestDto;
import com.aerilon.turfclan.facility.dto.FacilitiesMobileResponseDto;
import com.aerilon.turfclan.facility.dto.FacilityUpdateDto;
import com.aerilon.turfclan.facility.dto.SportDetailUpdateDto;
import com.aerilon.turfclan.partner.dto.FacilityRequestDto;
import com.aerilon.turfclan.partner.dto.SportDetailRequestDto;

import java.util.UUID;

public interface FacilityService {

    FacilitiesRequestDto getFacilityForUser(String userId);

    FacilitiesRequestDto getAllFacility();

    FacilitiesMobileResponseDto getAllFacilitiesForMobile(Double userLatitude, Double userLongitude, String sortBy, Double maxDistanceKm);

    FacilityRequestDto updateFacility(String userId, UUID facilityId, FacilityUpdateDto updateDto);

    FacilityRequestDto updateSportDetail(String userId, UUID facilityId, UUID sportId, SportDetailUpdateDto updateDto);

    FacilityRequestDto addSportDetailToFacility(String userId, UUID facilityId, SportDetailRequestDto sportDetailRequestDto);

    FacilityRequestDto addFacilityForUser(String userId, FacilityRequestDto facilityRequestDto);
}
