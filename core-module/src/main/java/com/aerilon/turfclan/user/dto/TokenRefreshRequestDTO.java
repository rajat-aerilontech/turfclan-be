package com.aerilon.turfclan.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TokenRefreshRequestDTO {

    /** The refresh token issued during OTP verification. */
    private String refreshToken;
}
