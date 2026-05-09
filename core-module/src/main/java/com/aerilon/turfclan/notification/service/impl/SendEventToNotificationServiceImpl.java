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
            String initiatorId = emailId != null && emailId.contains("@") ? emailId : null;
            EventIngestionDto eventIngestionDto = createEventIngestionDtoJoinWaitlist(joinWaitlistDto, initiatorId);
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

    @Override
    public void sendEventToNotificationServiceForPartnerWithUsQuery(com.aerilon.turfclan.web.dto.PartnerWithUsDto partnerWithUsDto, String input) {
        try {
            EventIngestionDto eventIngestionDto = new EventIngestionDto();
            Map<String, String> data = new HashMap<>();
            data.put("userName", partnerWithUsDto.getName());
            data.put("brandName", partnerWithUsDto.getBrandName());
            data.put("emailOrPhone", partnerWithUsDto.getEmail_phone());
            data.put("description", partnerWithUsDto.getDescription());
            String initiatorId = input != null && input.contains("@") ? input : null;
            createCommonEventIngestionDtoValues(eventIngestionDto, initiatorId, WEB_CUSTOMER, EventType.PARTNER_QUERY, data);
            eventIngestionPublisher.publish(eventIngestionDto);
            log.info("Successfully send event to notification service for PartnerWithUsDto");
        } catch (Exception e) {
            log.error("Failed to send event to notification service for PartnerWithUsDto", e);
            throw e;
        }
    }

    @Override
    public void sendEventToNotificationServiceForContactInquiry(com.aerilon.turfclan.web.dto.ContactInquiryDto contactInquiryDto, String input) {
        try {
            EventIngestionDto eventIngestionDto = new EventIngestionDto();
            Map<String, String> data = new HashMap<>();
            data.put("userName", contactInquiryDto.getName());
            data.put("contactBy", contactInquiryDto.getContactBy());
            data.put("email", contactInquiryDto.getEmail());
            data.put("phoneNumber", contactInquiryDto.getPhoneNumber());
            data.put("subject", contactInquiryDto.getSubject());
            data.put("description", contactInquiryDto.getDescription());
            String initiatorId = input != null && input.contains("@") ? input : null;
            createCommonEventIngestionDtoValues(eventIngestionDto, initiatorId, WEB_CUSTOMER, EventType.CONTACT_US_QUERY, data);
            eventIngestionPublisher.publish(eventIngestionDto);
            log.info("Successfully send event to notification service for ContactInquiryDto");
        } catch (Exception e) {
            log.error("Failed to send event to notification service for ContactInquiryDto", e);
            throw e;
        }
    }
}
