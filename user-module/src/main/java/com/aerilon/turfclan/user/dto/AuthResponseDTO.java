package com.aerilon.turfclan.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
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

    /** True if the user has not completed their profile yet — client should navigate to signup screen. */
    @JsonProperty("new_user")
    private boolean newUser;

    @JsonProperty("old_user")
    private boolean oldUser;

    private String message;
}
