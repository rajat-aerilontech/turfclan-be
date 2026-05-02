package com.aerilon.turfclan.entity;

import com.aerilon.turfclan.enums.NotificationDisplayType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnTransformer;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "t_notification", schema = "turfclan_schema")
@Getter
@Setter
public class NotificationEntity extends BaseAuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "notification_type")
    private String notificationType;

    @Column(name = "notification_body")
    private String notificationBody;

    @Column(name = "notification_title")
    private String notificationTitle;

    @Column(name = "source_app")
    private String sourceApp;

    @Column(name = "user_email", length = 100, nullable = false)
    private String userEmail;

    @Column(name = "category", length = 100)
    private String category;

    @Column(name = "channel")
    private List<String> channel;

    @Column(name = "read")
    private String read;

    @Enumerated(EnumType.STRING)
    @Column(name = "display_type", length = 50)
    private NotificationDisplayType displayType;

    @Column(name = "displayed")
    private boolean displayed;

    @Column(name = "event_data", columnDefinition = "jsonb")
    @ColumnTransformer(write = "?::jsonb")
    private String eventData;

    @Column(name = "event_id")
    private String eventId;

    @Column(name = "event_created_at")
    private LocalDateTime eventCreatedAt;

    @Column(name = "event_created_by")
    private String eventCreatedBy;
}
