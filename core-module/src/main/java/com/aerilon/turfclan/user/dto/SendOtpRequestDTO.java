package com.aerilon.turfclan.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SendOtpRequestDTO {

    @JsonProperty("phone_country_code")
    private String phoneCountryCode;

    @NotBlank(message = "Phone number must not be blank")
    @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be exactly 10 digits")
    @JsonProperty("phone_number")
    private String phoneNumber;
}
