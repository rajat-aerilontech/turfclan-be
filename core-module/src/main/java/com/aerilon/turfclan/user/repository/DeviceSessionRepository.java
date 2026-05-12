package com.aerilon.turfclan.user.repository;

import com.aerilon.turfclan.user.entity.DeviceSessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DeviceSessionRepository extends JpaRepository<DeviceSessionEntity, UUID> {
    Optional<DeviceSessionEntity> findByRefreshTokenHash(String refreshTokenHash);
    List<DeviceSessionEntity> findAllByUserIdAndRevokedFalse(UUID userId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM t_device_sessions WHERE expires_at < current_date - interval '30 days'", nativeQuery = true)
    int deleteExpiredSessionsOlderThan30Days();
}
