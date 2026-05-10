package com.aerilon.turfclan.user.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TokenRefreshResponseDTO {

    /** New short-lived access token. */
    private String accessToken;

    /** New refresh token (rotated on every refresh). */
    private String refreshToken;

    /** Access token validity in seconds. */
    private long expiresIn;
}
