package com.aerilon.turfclan.partner.dto;

import com.aerilon.turfclan.partner.enums.BusinessType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BusinessDetailDto {

    @NotBlank(message = "Business Name is required")
    private String businessName;

    @NotNull(message = "Business type is required")
    private BusinessType businessType;

    @Pattern(
            regexp = "^([0-9]{2}[A-Z]{5}[0-9]{4}[A-Z]{1}[1-9A-Z]{1}Z[0-9A-Z]{1})?$",
            message = "Invalid GST format"
    )
    private String gstNumber;

    @NotBlank(message = "PAN is required")
    @Pattern(
            regexp = "^([A-Z]{5}[0-9]{4}[A-Z]{1})",
            message = "Invalid PAN format"
    )
    private String panNumber;

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
}
