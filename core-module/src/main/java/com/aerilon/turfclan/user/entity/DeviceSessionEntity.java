package com.aerilon.turfclan.user.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "t_device_sessions", schema = "turfclan_schema", indexes = {
    @Index(name = "idx_device_sessions_user_id", columnList = "user_id"),
    @Index(name = "idx_device_sessions_refresh_hash", columnList = "refresh_token_hash")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeviceSessionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID sessionId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    private String deviceId;
    private String deviceName;
    private String platform;

    @Column(name = "refresh_token_hash", nullable = false, unique = true)
    private String refreshTokenHash;

    private String ipAddress;
    private String userAgent;

    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @Column(nullable = false)
    private LocalDateTime expiresAt;
    
    private LocalDateTime lastUsedAt;

    private boolean revoked;
    private LocalDateTime revokedAt;
}
