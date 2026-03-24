package com.aerilon.turfclan.jwt;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(JwtProperties.class)
public class JwtConfig {
    // Enables JwtProperties @ConfigurationProperties binding and registers it as a bean.
}
