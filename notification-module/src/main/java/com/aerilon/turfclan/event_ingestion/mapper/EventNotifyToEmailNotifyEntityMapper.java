package com.aerilon.turfclan.event_ingestion.mapper;

import com.aerilon.turfclan.event_ingestion.entity.EventNotificationEntity;
import com.aerilon.turfclan.event_ingestion.entity.NotificationEmailEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class EventNotifyToEmailNotifyEntityMapper {

    private final static String SYSTEM = "system";

    @Autowired
    protected ObjectMapper objectMapper;

    @Mapping(target = "id", expression = "java(java.util.UUID.randomUUID())")
    @Mapping(target = "notificationType", source = "eventType")
    @Mapping(target = "notificationBody", source = "body")
    @Mapping(target = "notificationTitle", source = "title")
    @Mapping(target = "category", source = "notificationCategory")
    @Mapping(target = "read", constant = "false")
    @Mapping(target = "displayType", source = "notificationDisplayType")
    @Mapping(target = "eventData", source = "data")
    @Mapping(target = "notificationAction", expression = "java(mergeNotificationAction(event.getActionUrl(), event.getActionName(), event.getActionType()))")
    @Mapping(target = "severity", source = "notificationSeverity")
    @Mapping(target = "displayed", constant = "false")
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "eventCreatedBy", constant = SYSTEM)
    public abstract NotificationEmailEntity toEntity(EventNotificationEntity event);

    @Named("mergeNotificationAction")
    protected String mergeNotificationAction(String url, String name, String type) {
        try {
            var node = objectMapper.createObjectNode();
            node.put("url", url);
            node.put("name", name);
            node.put("type", type);
            return objectMapper.writeValueAsString(node);
        } catch (Exception e) {
            throw new RuntimeException("Failed to build notificationAction JSON", e);
        }
    }
}
