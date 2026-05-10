package com.aerilon.turfclan.facility.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Response wrapper for mobile facility listing
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
public class FacilitiesMobileResponseDto {
    private List<FacilityMobileResponseDto> facilities;
    private Integer totalCount;
}

