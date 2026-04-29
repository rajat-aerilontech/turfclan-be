package com.aerilon.turfclan.partner.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FacilityRequestDto {

    @NotBlank(message = "Facility Name is required")
    private String facilityName;
    @NotBlank(message = "Description is required")
    private String description;
    @NotEmpty(message = "At least one Photo of facility image is required")
    private List<String> facilityPhotos;
    @NotBlank(message = "Address Line is required")
    private String addressLine1;

    private String addressLine2;

    @NotBlank(message = "Landmark is required")
    private String landmark;

    @NotBlank(message = "Pincode is required")
    @Pattern(
            regexp = "^([1-9][0-9]{5})",
            message = "Pincode must be 6 digits"
    )
    private String pincode;

    @NotBlank(message = "City is required")
    private String city;

    @NotBlank(message = "State is required")
    private String state;
    @NotNull(message = "Latitude is required")
    private Double latitude;
    @NotNull(message = "Latitude is required")
    private Double longitude;
    @Valid
    @NotEmpty(message = "At least one sport detail is required")
    private List<@Valid SportDetailRequestDto> sports;
}
