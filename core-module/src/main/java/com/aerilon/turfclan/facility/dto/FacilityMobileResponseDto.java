package com.aerilon.turfclan.facility.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import com.aerilon.turfclan.dto.S3ImageResponseDto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Mobile-specific DTO for Facility response with location-based information
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
public class FacilityMobileResponseDto {

    private UUID id;
    private String facilityName;
    private String description;
    private List<S3ImageResponseDto> facilityPhotos;
    private String addressLine1;
    private String addressLine2;
    private String landmark;
    private String pincode;
    private String city;
    private String state;
    private Double latitude;
    private Double longitude;
    private Boolean canBeBooked;

    // Mobile-specific fields
    private Double distanceKm;  // Distance from user's current location in KM
    private BigDecimal lowestPrice;  // Lowest price among all sports available at this facility
    private String lowestPriceCurrency;

    // Sports details
    private List<SubFacilityRequestDto> sports;
}

