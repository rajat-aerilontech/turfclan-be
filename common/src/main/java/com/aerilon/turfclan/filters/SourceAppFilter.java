package com.aerilon.turfclan.filters;

import com.aerilon.turfclan.response.ErrorResponse;
import com.aerilon.turfclan.security.AuthenticatedUser;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SourceAppFilter extends OncePerRequestFilter {

    public static final String SOURCE_APP_HEADER = "source-app";
    public static final String TURF_ADMIN = "turf-admin";
    public static final String TURF_MOBILE = "turf-mobile";
    public static final String TURF_PARTNER = "turf-partner";
    public static final String TURF_WEB = "turf-web";

    private static final List<String> PUBLIC_PATH_PREFIXES = List.of(
            "/v3/api-docs",
            "/swagger-ui",
            "/swagger-ui.html",
            "/actuator"
    );

    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException
    {
        if (HttpMethod.OPTIONS.matches(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }
        if (isSourceAppOnlyPath(request)) {
            authenticateSourceAppOnlyRequest(request, response, filterChain);
            return;
        }

        if (!isApiPath(request) || isPublicPath(request) || isPreflight(request) || !isAuthenticatedRequest()) {
            filterChain.doFilter(request, response);
            return;
        }

        String sourceApp = request.getHeader(SOURCE_APP_HEADER);
        if (sourceApp == null || sourceApp.isBlank()) {
            writeBadRequest(response, "Missing required header: source-app");
            return;
        }

        String normalizedSourceApp = sourceApp.trim().toLowerCase();
        SimpleGrantedAuthority authority = resolveRole(normalizedSourceApp);
        if (authority == null) {
            writeBadRequest(response, "Invalid source-app.");
            return;
        }

        Authentication existing = SecurityContextHolder.getContext().getAuthentication();
        String roleFromSourceApp = extractRoleFromAuthority(normalizedSourceApp);
        
        // Create updated principal with role from source-app
        Object existingPrincipal = existing.getPrincipal();
        Object newPrincipal = existingPrincipal;
        
        if (existingPrincipal instanceof AuthenticatedUser) {
            AuthenticatedUser user = (AuthenticatedUser) existingPrincipal;
            newPrincipal = new AuthenticatedUser(user.userId(), roleFromSourceApp);
        }
        
        UsernamePasswordAuthenticationToken remapped = new UsernamePasswordAuthenticationToken(
                newPrincipal,
                existing.getCredentials(),
            List.of(authority)
        );
        remapped.setDetails(existing.getDetails());
        SecurityContextHolder.getContext().setAuthentication(remapped);

        request.setAttribute(SOURCE_APP_HEADER, normalizedSourceApp);
        filterChain.doFilter(request, response);
    }

    private void authenticateSourceAppOnlyRequest(HttpServletRequest request,
                                                  HttpServletResponse response,
                                                  FilterChain filterChain) throws ServletException, IOException {
        if (isPreflight(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        String sourceApp = request.getHeader(SOURCE_APP_HEADER);
        if (sourceApp == null || sourceApp.isBlank()) {
            writeUnauthorized(response, "Missing required header: source-app");
            return;
        }

        String normalizedSourceApp = sourceApp.trim().toLowerCase();
        SimpleGrantedAuthority authority = resolveRole(normalizedSourceApp);
        if (authority == null) {
            writeUnauthorized(response, "Invalid source-app.");
            return;
        }

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                normalizedSourceApp,
                null,
                List.of(authority)
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        request.setAttribute(SOURCE_APP_HEADER, normalizedSourceApp);
        filterChain.doFilter(request, response);
    }

    private SimpleGrantedAuthority resolveRole(String sourceApp) {
        return switch (sourceApp) {
            case TURF_ADMIN -> new SimpleGrantedAuthority("ROLE_TA_USER");
            case TURF_MOBILE -> new SimpleGrantedAuthority("ROLE_TM_USER");
            case TURF_PARTNER -> new SimpleGrantedAuthority("ROLE_TM_PARTNER");
            case TURF_WEB -> new SimpleGrantedAuthority("ROLE_TM_WEB");
            default -> null;
        };
    }

    private String extractRoleFromAuthority(String sourceApp) {
        return switch (sourceApp) {
            case TURF_ADMIN -> "TA_USER";
            case TURF_MOBILE -> "TM_USER";
            case TURF_PARTNER -> "TM_PARTNER";
            case TURF_WEB -> "TM_WEB";
            default -> null;
        };
    }

    private boolean isApiPath(HttpServletRequest request) {
        return request.getServletPath().startsWith("/api/v1/");
    }

    private boolean isPublicPath(HttpServletRequest request) {
        String servletPath = request.getServletPath();
        return PUBLIC_PATH_PREFIXES.stream().anyMatch(servletPath::startsWith);
    }

    private boolean isSourceAppOnlyPath(HttpServletRequest request) {
        String servletPath = request.getServletPath();
        return servletPath.startsWith("/api/v1/users/otp/")
                || servletPath.startsWith("/api/v1/auth/refresh")
                || servletPath.startsWith("/api/v1/web/");
    }

    private boolean isPreflight(HttpServletRequest request) {
        return HttpMethod.OPTIONS.matches(request.getMethod());
    }

    private boolean isAuthenticatedRequest() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null
                && authentication.isAuthenticated()
                && authentication.getPrincipal() != null
                && !"anonymousUser".equals(authentication.getPrincipal());
    }

    private void writeBadRequest(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getWriter(), new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                message,
                System.currentTimeMillis()
        ));
    }

    private void writeUnauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getWriter(), new ErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                message,
                System.currentTimeMillis()
        ));
    }
}
