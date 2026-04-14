package com.aerilon.turfclan.filters;

import com.aerilon.turfclan.response.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SelectedSportExperienceFilter extends OncePerRequestFilter {

    public static final String SELECTED_SPORT_EXPERIENCE_HEADER = "selected-sport-experience";

    private static final List<String> PUBLIC_PATH_PREFIXES = List.of(
            "/api/v1/users/otp/",
            "/api/v1/auth/",
            "/v3/api-docs",
            "/swagger-ui",
            "/swagger-ui.html",
            "/actuator"
    );

    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        if (!isApiPath(request)
                || isPublicPath(request)
                || isPreflight(request)
                || isSportsAssociationAdminWriteCall(request)
                || !isAuthenticatedRequest()) {
            filterChain.doFilter(request, response);
            return;
        }

        String selectedSportExperience = request.getHeader(SELECTED_SPORT_EXPERIENCE_HEADER);
        if (selectedSportExperience == null || selectedSportExperience.isBlank()) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            objectMapper.writeValue(response.getWriter(), new ErrorResponse(
                    HttpStatus.BAD_REQUEST.value(),
                    "Missing required header: selected-sport-experience",
                    System.currentTimeMillis()
            ));
            return;
        }

        request.setAttribute(SELECTED_SPORT_EXPERIENCE_HEADER, selectedSportExperience.trim());
        filterChain.doFilter(request, response);
    }

    private boolean isApiPath(HttpServletRequest request) {
        return request.getServletPath().startsWith("/api/v1/");
    }

    private boolean isPublicPath(HttpServletRequest request) {
        String servletPath = request.getServletPath();
        return PUBLIC_PATH_PREFIXES.stream().anyMatch(servletPath::startsWith);
    }

    private boolean isPreflight(HttpServletRequest request) {
        return HttpMethod.OPTIONS.matches(request.getMethod());
    }

    private boolean isSportsAssociationAdminWriteCall(HttpServletRequest request) {
        String path = request.getServletPath();
        String method = request.getMethod();
        if (!path.startsWith("/api/v1/sports/associations")) {
            return false;
        }
        return HttpMethod.POST.matches(method)
                || HttpMethod.PUT.matches(method)
                || HttpMethod.DELETE.matches(method);
    }

    private boolean isAuthenticatedRequest() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null
                && authentication.isAuthenticated()
                && authentication.getPrincipal() != null
                && !"anonymousUser".equals(authentication.getPrincipal());
    }
}
