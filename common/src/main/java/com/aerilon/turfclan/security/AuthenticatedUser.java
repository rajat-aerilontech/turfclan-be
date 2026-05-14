package com.aerilon.turfclan.security;

/**
 * Record representing an authenticated user with userId and role information.
 */
public record AuthenticatedUser(
        String userId,
        String role
) {}
