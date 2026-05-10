package com.aerilon.turfclan.event_ingestion.repository;

import com.aerilon.turfclan.event_ingestion.entity.RuleEntity;
import com.aerilon.turfclan.event_ingestion.enums.EventType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RuleRepository extends JpaRepository<RuleEntity, UUID> {
    RuleEntity findBySourceAppAndEventType(String sourceApp, EventType eventType);
}
