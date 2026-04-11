package com.aerilon.turfclan.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OtpRequestDTO {

    @JsonProperty("phone_country_code")
    private String phoneCountryCode;

    @JsonProperty("phone_number")
    private String phoneNumber;
}
