package com.aerilon.turfclan.event_ingestion.entity;

import com.aerilon.turfclan.entity.BaseAuditableEntity;
import com.aerilon.turfclan.enums.Severity;
import com.aerilon.turfclan.event_ingestion.enums.EventType;
import com.aerilon.turfclan.event_ingestion.enums.NotificationCategory;
import com.aerilon.turfclan.event_ingestion.enums.NotificationDisplayType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnTransformer;

import java.util.UUID;

@Entity
@Table(name = "t_notification_email", schema = "turfclan_notification_schema")
@Getter
@Setter
public class NotificationEmailEntity extends BaseAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "notification_type")
    private EventType notificationType;

    @Column(name = "notification_body")
    private String notificationBody;

    @Column(name = "notification_title")
    private String notificationTitle;

    @Column(name = "source_app")
    private String sourceApp;

    @Column(name = "user_email")
    private String userEmail;

    @Column(name = "category")
    private NotificationCategory category;

    @Column(name = "read")
    private boolean read;

    @Column(name = "display_type")
    private NotificationDisplayType displayType;

    @Column(name = "displayed")
    private String displayed;

    @Column(name = "event_data", columnDefinition = "jsonb")
    @ColumnTransformer(write = "?::jsonb")
    private String eventData;

    @Column(name = "severity")
    private Severity severity;

    @Column(name = "notification_action", columnDefinition = "jsonb")
    @ColumnTransformer(write = "?::jsonb")
    private String notificationAction;

    @Column(name = "event_id")
    private UUID eventId;

    @Column(name = "country_code")
    private String countryCode;

    @Column(name = "event_created_by")
    private String eventCreatedBy;

    @Column(name = "event_created_at")
    private String eventCreatedAt;
}
