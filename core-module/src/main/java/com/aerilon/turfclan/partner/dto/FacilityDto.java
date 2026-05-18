package com.aerilon.turfclan.partner.dto;

import com.aerilon.turfclan.dto.S3ImageResponseDto;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FacilityDto {
    private String facilityId;
    private String facilityName;
    private String description;
    private List<S3ImageResponseDto> facilityPhotos;
    private String addressLine1;
    private String addressLine2;
    private String landmark;
    private String pincode;
    private String city;
    private String state;
    private Boolean canBeBooked;
    private Double latitude;
    private Double longitude;
    private List<SubFacilityDto> subFacilities;
}