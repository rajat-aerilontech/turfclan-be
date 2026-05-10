package com.aerilon.turfclan.facility.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FacilityUpdateDto {

    private String facilityName;
    private String description;
    private List<MultipartFile> facilityPhotos;
    private String addressLine1;
    private String addressLine2;
    private String landmark;
    private String pincode;
    private String city;
    private String state;
    private Double latitude;
    private Double longitude;
}

