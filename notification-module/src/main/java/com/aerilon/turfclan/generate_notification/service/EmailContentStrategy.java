package com.aerilon.turfclan.generate_notification.service;

import com.aerilon.turfclan.event_ingestion.entity.EventNotificationEntity;
import com.aerilon.turfclan.event_ingestion.enums.EventType;
import com.aerilon.turfclan.generate_notification.dto.NotificationRecipientInfoDto;
import org.thymeleaf.context.Context;

public interface EmailContentStrategy {

    String getTemplateName(NotificationRecipientInfoDto recipientInfo);

    Context buildContext(EventNotificationEntity event, NotificationRecipientInfoDto recipientInfo, String langCountryCode);

    String buildSubject(EventNotificationEntity event, Context context);

    EventType getEventType();
}
