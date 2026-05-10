package com.aerilon.turfclan.event_ingestion.repository;

import com.aerilon.turfclan.event_ingestion.entity.EventNotificationEntity;
import com.aerilon.turfclan.event_ingestion.enums.EventStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Repository
public interface EventNotificationRepository extends JpaRepository<EventNotificationEntity, UUID> {
    @Modifying
    @Transactional
    @Query("""
        UPDATE EventNotificationEntity e SET 
            e.status = :status,
            e.failedRecipients = :failedRecipients,
            e.batchCounter = :batchCounter
        WHERE 
            e.eventId = :eventId
            AND e.notificationChannel = :channel
    """)
    int updateStatusAndBatchCounter(
            @Param("status") EventStatus status,
            @Param("failedRecipients") String[] failedRecipients,
            @Param("batchCounter") int batchCounter,
            @Param("eventId") UUID eventId,
            @Param("channel") String channel
    );
}
