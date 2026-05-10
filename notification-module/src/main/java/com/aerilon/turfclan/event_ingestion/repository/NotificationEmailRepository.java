package com.aerilon.turfclan.event_ingestion.repository;

import com.aerilon.turfclan.event_ingestion.entity.NotificationEmailEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface NotificationEmailRepository extends JpaRepository<NotificationEmailEntity, UUID> {
}