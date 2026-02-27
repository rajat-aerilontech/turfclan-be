package com.aerilon.turfclan.user.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuthResponseDTO {

    /** Full user profile returned after successful authentication. */
    private UserDTO user;

    /** Short-lived JWT for protecting API resources. */
    private String accessToken;

    /** Long-lived JWT used to obtain a new access token. */
    private String refreshToken;

    /** Access token validity period in seconds (convenience field for clients). */
    private long expiresIn;
}
