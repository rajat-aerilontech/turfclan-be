package com.aerilon.turfclan.jwt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    /**
     * Base64-encoded HS256 signing secret (min 256 bits / 32 bytes before encoding).
     * Override via JWT_SECRET environment variable in production.
     */
    private String secret;

    /**
     * Access token validity in milliseconds. Default: 15 minutes (900_000 ms).
     */
    private long accessTokenExpiryMs = 900_000L;

    /**
     * Refresh token validity in milliseconds. Default: 7 days (604_800_000 ms).
     */
    private long refreshTokenExpiryMs = 604_800_000L;
}
