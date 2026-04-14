package com.aerilon.turfclan.config;

import com.aerilon.turfclan.filters.JwtAuthenticationFilter;
import com.aerilon.turfclan.filters.SelectedSportExperienceFilter;
import com.aerilon.turfclan.filters.SourceAppFilter;
import com.aerilon.turfclan.exception.UnauthorizedAccessException;
import com.aerilon.turfclan.response.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final SourceAppFilter sourceAppFilter;
    private final SelectedSportExperienceFilter selectedSportExperienceFilter;
    private final ObjectMapper objectMapper;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(
                                "/api/v1/users/otp/**",
                                "/api/v1/auth/**",
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/actuator/**"
                        ).permitAll()
                        .requestMatchers("/api/v1/users/**").authenticated()
                        .requestMatchers("/api/v1/sports/**").authenticated()
                        .requestMatchers("/api/v1/sports-directory/**").authenticated()
                        .anyRequest().permitAll()
                )
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) -> {
                            UnauthorizedAccessException unauthorizedAccessException =
                                    new UnauthorizedAccessException("Missing, invalid, or expired access token");
                            response.setStatus(HttpStatus.UNAUTHORIZED.value());
                            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                            objectMapper.writeValue(response.getWriter(), new ErrorResponse(
                                    HttpStatus.UNAUTHORIZED.value(),
                                    unauthorizedAccessException.getMessage(),
                                    System.currentTimeMillis()
                            ));
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            UnauthorizedAccessException unauthorizedAccessException =
                                    new UnauthorizedAccessException("Unauthorized access for this source-app and role");
                            response.setStatus(HttpStatus.UNAUTHORIZED.value());
                            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                            objectMapper.writeValue(response.getWriter(), new ErrorResponse(
                                    HttpStatus.UNAUTHORIZED.value(),
                                    unauthorizedAccessException.getMessage(),
                                    System.currentTimeMillis()
                            ));
                        })
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(sourceAppFilter, JwtAuthenticationFilter.class)
                .addFilterAfter(selectedSportExperienceFilter, SourceAppFilter.class);

        return http.build();
    }
}
