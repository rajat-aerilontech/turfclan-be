package com.aerilon.turfclan.user.job;

import com.aerilon.turfclan.user.repository.DeviceSessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class DeviceSessionCleanupJob {

    private final DeviceSessionRepository deviceSessionRepository;

    /**
     * Runs every day at 2:00 AM to prune expired sessions.
     * Deletes sessions where the expiry date is older than 30 days.
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void cleanupExpiredSessions() {
        log.info("Starting scheduled cleanup of expired device sessions.");
        try {
            // Note: If you want to do a direct delete query, it's more efficient.
            // But we can also use repository methods. 
            // The method logic to be called here needs to be in the repository.
            int deletedCount = deviceSessionRepository.deleteExpiredSessionsOlderThan30Days();
            log.info("Successfully deleted {} expired device sessions.", deletedCount);
        } catch (Exception e) {
            log.error("Failed to clean up expired device sessions.", e);
        }
    }
}
