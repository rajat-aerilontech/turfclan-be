package com.aerilon.turfclan.filters;

import com.aerilon.turfclan.jwt.JwtService;
import com.aerilon.turfclan.response.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final List<String> SOURCE_APP_ONLY_PATH_PREFIXES = List.of(
        "/api/v1/users/otp/",
        "/api/v1/auth/refresh"
    );

    private final JwtService jwtService;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        try {
            io.jsonwebtoken.Claims claims = jwtService.validateToken(token);
            String userId = claims.getSubject();
            
            Object audObj = claims.get("aud");
            String role = null;
            if (audObj instanceof String) {
                role = (String) audObj;
            } else if (audObj instanceof java.util.Collection) {
                java.util.Collection<?> col = (java.util.Collection<?>) audObj;
                if (!col.isEmpty()) {
                    role = col.iterator().next().toString();
                }
            }

            java.util.List<org.springframework.security.core.GrantedAuthority> authorities = java.util.Collections.emptyList();
            if (role != null) {
                authorities = java.util.Collections.singletonList(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_TM_" + role.toUpperCase()));
            }

            if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userId, null, authorities);
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

            filterChain.doFilter(request, response);
        } catch (JwtException | IllegalArgumentException ex) {
            log.warn("JWT authentication failed: {}", ex.getMessage());
            SecurityContextHolder.clearContext();

            if (isSourceAppOnlyPath(request)) {
                filterChain.doFilter(request, response);
                return;
            }

            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            objectMapper.writeValue(response.getWriter(), new ErrorResponse(
                    HttpStatus.UNAUTHORIZED.value(),
                    "Invalid or expired access token",
                    System.currentTimeMillis()
            ));
        }
    }

    private boolean isSourceAppOnlyPath(HttpServletRequest request) {
        String servletPath = request.getServletPath();
        return SOURCE_APP_ONLY_PATH_PREFIXES.stream().anyMatch(servletPath::startsWith);
    }
}
