package com.aerilon.turfclan.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class JwtService {

    private final JwtProperties jwtProperties;

    // -----------------------------------------------------------------------
    // Token generation
    // -----------------------------------------------------------------------

    /**
     * Generates a short-lived access token.
     *
     * @param subject the principal identity (e.g. user UUID as string)
     * @param claims  additional claims to embed in the token (e.g. roles, phone)
     * @return signed JWT string
     */
    public String generateAccessToken(String subject, Map<String, Object> claims) {
        return buildToken(subject, claims, jwtProperties.getAccessTokenExpiryMs());
    }

    /**
     * Generates a long-lived refresh token with only the subject claim.
     *
     * @param subject the principal identity (e.g. user UUID as string)
     * @return signed JWT string
     */
    public String generateRefreshToken(String subject) {
        return buildToken(subject, Map.of(), jwtProperties.getRefreshTokenExpiryMs());
    }

    // -----------------------------------------------------------------------
    // Token validation
    // -----------------------------------------------------------------------

    /**
     * Parses and validates a JWT, returning its {@link Claims}.
     * Throws a JJWT exception if the token is expired or malformed.
     *
     * @param token the JWT string
     * @return parsed claims
     */
    public Claims validateToken(String token) {
        return Jwts.parser()
                .verifyWith(signingKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Convenience method — validates the token and returns its subject claim.
     * Callers outside the {@code common} module should use this instead of
     * {@link #validateToken(String)} to avoid a direct dependency on the JJWT API.
     *
     * @param token the JWT string
     * @return subject (e.g. user UUID)
     * @throws io.jsonwebtoken.JwtException if the token is invalid or expired
     */
    public String getSubject(String token) {
        return validateToken(token).getSubject();
    }

    // -----------------------------------------------------------------------
    // Private helpers
    // -----------------------------------------------------------------------

    private String buildToken(String subject, Map<String, Object> extraClaims, long expiryMs) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .claims(extraClaims)
                .subject(subject)
                .issuedAt(new Date(now))
                .expiration(new Date(now + expiryMs))
                .signWith(signingKey(), Jwts.SIG.HS256)
                .compact();
    }

    private SecretKey signingKey() {
        byte[] keyBytes = Base64.getDecoder().decode(jwtProperties.getSecret());
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
