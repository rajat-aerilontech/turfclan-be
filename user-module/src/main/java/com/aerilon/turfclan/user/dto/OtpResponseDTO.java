package com.aerilon.turfclan.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class OtpResponseDTO {

    private String message;

    @JsonProperty("expires_at")
    private LocalDateTime expiresAt;
}
