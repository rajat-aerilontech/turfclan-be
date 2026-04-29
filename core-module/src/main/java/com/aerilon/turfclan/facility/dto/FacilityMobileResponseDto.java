package com.aerilon.turfclan.facility.dto;

import com.aerilon.turfclan.partner.dto.SportDetailRequestDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

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
    private List<String> facilityPhotos;
    private String addressLine1;
    private String addressLine2;
    private String landmark;
    private String pincode;
    private String city;
    private String state;
    private Double latitude;
    private Double longitude;

    // Mobile-specific fields
    private Double distanceKm;  // Distance from user's current location in KM
    private BigDecimal lowestPrice;  // Lowest price among all sports available at this facility
    private String lowestPriceCurrency;

    // Sports details
    private List<SportDetailRequestDto> sports;
}

