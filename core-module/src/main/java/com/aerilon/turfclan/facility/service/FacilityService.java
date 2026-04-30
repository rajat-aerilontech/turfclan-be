package com.aerilon.turfclan.facility.service;

import com.aerilon.turfclan.facility.dto.FacilitiesRequestDto;
import com.aerilon.turfclan.facility.dto.FacilitiesMobileResponseDto;
import com.aerilon.turfclan.facility.dto.FacilityUpdateDto;
import com.aerilon.turfclan.facility.dto.SubFacilityUpdateDto;
import com.aerilon.turfclan.facility.dto.FacilityRequestDto;
import com.aerilon.turfclan.facility.dto.SubFacilityRequestDto;

import java.util.UUID;

public interface FacilityService {

    FacilitiesRequestDto getFacilityForUser(String userId);

    FacilitiesRequestDto getAllFacility();

    FacilitiesMobileResponseDto getAllFacilitiesForMobile(Double userLatitude, Double userLongitude, String sortBy, Double maxDistanceKm);

    FacilityRequestDto updateFacility(String userId, UUID facilityId, FacilityUpdateDto updateDto);

    FacilityRequestDto updateSubFacility(String userId, UUID facilityId, UUID sportId, SubFacilityUpdateDto updateDto);

    FacilityRequestDto addSubFacilityToFacility(String userId, UUID facilityId, SubFacilityRequestDto subFacilityRequestDto);

    FacilityRequestDto addFacilityForUser(String userId, FacilityRequestDto facilityRequestDto);
}
