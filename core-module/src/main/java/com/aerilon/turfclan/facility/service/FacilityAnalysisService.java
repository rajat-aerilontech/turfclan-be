package com.aerilon.turfclan.facility.service;

import com.aerilon.turfclan.facility.dto.FacilityAnalysisResponseDto;

import java.time.LocalDate;
import java.util.UUID;

public interface FacilityAnalysisService {
    FacilityAnalysisResponseDto getFacilityAnalysis(String userId, UUID facilityId, LocalDate startDate, LocalDate endDate);
}
