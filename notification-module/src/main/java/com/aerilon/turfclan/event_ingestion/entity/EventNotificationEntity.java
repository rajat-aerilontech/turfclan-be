package com.aerilon.turfclan.event_ingestion.entity;

import com.aerilon.turfclan.entity.BaseAuditableEntity;
import com.aerilon.turfclan.enums.*;
import com.aerilon.turfclan.event_ingestion.enums.EventStatus;
import com.aerilon.turfclan.event_ingestion.enums.EventType;
import com.aerilon.turfclan.event_ingestion.enums.NotificationCategory;
import com.aerilon.turfclan.event_ingestion.enums.NotificationDisplayType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnTransformer;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "t_event_notifications", schema = "turfclan_notification_schema")
@Getter
@Setter
public class EventNotificationEntity extends BaseAuditableEntity {

    @Id
    @Column(name = "event_notification_id")
    private UUID eventNotificationId;

    @Column(name = "event_id")
    private UUID eventId;

    @Enumerated(EnumType.STRING)
    @Column(name = "eventType")
    private EventType eventType;

    @Column(name = "source_app")
    private String sourceApp;

    @Enumerated(EnumType.STRING)
    @Column(name = "notification_category")
    private NotificationCategory notificationCategory;

    @Column(name = "notification_channel")
    private String notificationChannel;

    @Enumerated(EnumType.STRING)
    @Column(name = "notification_display_type", length = 50)
    private NotificationDisplayType notificationDisplayType;

    @Enumerated(EnumType.STRING)
    @Column(name = "notification_severity", length = 50)
    private Severity notificationSeverity;

    @Column(name = "title")
    private String title;

    @Column(name = "body")
    private String body;

    @Column(name = "action_type")
    private String actionType;

    @Column(name = "action_url")
    private String actionUrl;

    @Column(name = "action_name")
    private String actionName;

    @Column(name = "rule_name")
    private String ruleName;

    @Column(name = "data", columnDefinition = "jsonb")
    @ColumnTransformer(write = "?::jsonb")
    private String data;

    @Column(name = "event_created_at")
    private String eventCreatedAt;

    @Column(name = "event_created_by")
    private String eventCreatedBy;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private EventStatus status;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "failed_recipients", columnDefinition = "text[]")
    private String[] failedRecipients;

    @Column(name = "batch_counter")
    private int batchCounter;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "target_audience", columnDefinition = "text[]")
    private String[] targetAudience;

    @Column(name = "include_initiator")
    private Boolean includeInitiator;
}
