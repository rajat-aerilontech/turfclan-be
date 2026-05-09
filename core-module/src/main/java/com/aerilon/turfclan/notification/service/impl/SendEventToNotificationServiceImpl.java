package com.aerilon.turfclan.notification.service.impl;

import com.aerilon.turfclan.dto.EventIngestionDto;
import com.aerilon.turfclan.event_ingestion.enums.EventType;
import com.aerilon.turfclan.enums.Severity;
import com.aerilon.turfclan.notification.publisher.EventIngestionPublisher;
import com.aerilon.turfclan.notification.service.SendEventToNotificationService;

import com.aerilon.turfclan.web.dto.JoinWaitlistDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SendEventToNotificationServiceImpl implements SendEventToNotificationService {

    private static final String SOURCE_APP = "TF_WEB";
    private static final String ERROR_CREATING_EVENT_INGESTION_DTO_LOG = "Failed to send event to notification service for JoinWaitlistDto";
    private static final String WEB_CUSTOMER = "WEB_CUSTOMER";
    private final EventIngestionPublisher eventIngestionPublisher;

    @Override
    public void sendEventToNotificationServiceForJoinWaitlist(JoinWaitlistDto joinWaitlistDto, String emailId) {
            EventIngestionDto eventIngestionDto = createEventIngestionDtoJoinWaitlist(joinWaitlistDto, emailId);
            eventIngestionPublisher.publish(eventIngestionDto);
            log.info("Successfully send event to notification service for JoinWaitlistDto");
    }

    private EventIngestionDto createEventIngestionDtoJoinWaitlist(JoinWaitlistDto joinWaitlistDto, String emailId) {
        try {
            EventIngestionDto eventIngestionDto = new EventIngestionDto();
            Map<String, String> data = new HashMap<>();
            data.put("userName", joinWaitlistDto.getEmail_phone());
            createCommonEventIngestionDtoValues(eventIngestionDto, emailId, WEB_CUSTOMER, EventType.JOIN_WAITLIST, data);
            return eventIngestionDto;
        } catch (Exception e) {
            log.error(ERROR_CREATING_EVENT_INGESTION_DTO_LOG, e);
            throw e;
        }
    }

    private void createCommonEventIngestionDtoValues(EventIngestionDto eventIngestionDto, String emailId, String initiatorType, EventType eventType, Map<String, String> data) {
        UUID eventId = UUID.randomUUID();

        eventIngestionDto.setEventId(eventId.toString());
        eventIngestionDto.setEventType(eventType.toString());
        eventIngestionDto.setEventCreatedAt(Instant.now().toString());
        eventIngestionDto.setSourceApp(SOURCE_APP);
        eventIngestionDto.setSeverity(Severity.INFO);
        eventIngestionDto.setInitiatorId(emailId);
        eventIngestionDto.setInitiatorType(initiatorType);
        eventIngestionDto.setData(data);
    }
}
