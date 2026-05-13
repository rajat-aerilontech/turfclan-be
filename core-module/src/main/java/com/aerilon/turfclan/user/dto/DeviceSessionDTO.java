package com.aerilon.turfclan.user.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class DeviceSessionDTO {
    private UUID sessionId;
    private String deviceId;
    private String deviceName;
    private String platform;
    private String ipAddress;
    private String userAgent;
    private LocalDateTime createdAt;
    private LocalDateTime lastUsedAt;
}
