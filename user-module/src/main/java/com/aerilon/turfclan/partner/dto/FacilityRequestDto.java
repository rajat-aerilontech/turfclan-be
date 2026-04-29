package com.aerilon.turfclan.partner.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
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
    @Valid
    @NotEmpty(message = "At least one sport detail is required")
    private List<@Valid SportDetailRequestDto> sports;
}
