package com.aerilon.turfclan.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OtpVerifyRequestDTO {

    @JsonProperty("phone_number")
    private String phoneNumber;

    @JsonProperty("otp_code")
    private String otpCode;
}
