package com.aerilon.turfclan.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Utility component for security-related operations.
 * Provides helpers to avoid injecting Authentication into every controller.
 */
@Component
@RequiredArgsConstructor
public class SecurityUtils {

    /**
     * Get the current authenticated user's ID from the security context.
     *
     * @return the user ID of the currently authenticated user
     */
    public String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof AuthenticatedUser) {
            AuthenticatedUser user = (AuthenticatedUser) authentication.getPrincipal();
            return user.userId();
        }
        // Fallback for backward compatibility
        return authentication != null ? authentication.getName() : null;
    }

    /**
     * Get the current authenticated user object from the security context.
     *
     * @return the AuthenticatedUser object or null if not authenticated
     */
    public AuthenticatedUser getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof AuthenticatedUser) {
            return (AuthenticatedUser) authentication.getPrincipal();
        }
        return null;
    }

    /**
     * Get the current authenticated user's role from the security context.
     *
     * @return the role of the currently authenticated user or null if not available
     */
    public String getCurrentUserRole() {
        AuthenticatedUser user = getCurrentUser();
        return user != null ? user.role() : null;
    }
}
