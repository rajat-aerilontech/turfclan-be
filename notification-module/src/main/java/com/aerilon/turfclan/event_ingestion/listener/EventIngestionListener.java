package com.aerilon.turfclan.event_ingestion.listener;

import com.aerilon.turfclan.dto.EventIngestionDto;
import com.aerilon.turfclan.event_ingestion.dto.EventDto;
import com.aerilon.turfclan.event_ingestion.enums.EventType;
import com.aerilon.turfclan.event_ingestion.service.EventApplyRuleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventIngestionListener {

    private final EventApplyRuleService eventApplyRuleService;

    @Async("notificationTaskExecutor")
    @EventListener
    public void handleEventIngestionOnEventNotificationReceived(EventIngestionDto eventIngestionDto) {
        log.info("Processing ingestedEvent for eventId: {}", eventIngestionDto.getEventId());

        EventDto eventDto = toEventDto(eventIngestionDto);

        eventApplyRuleService.applyRules(eventDto)
                .doOnSubscribe(subscription -> log.debug("Started async ingestion for eventId: {}", eventDto.getEventId()))
                .subscribe(
                        ignored -> {
                        },
                        e -> log.error("Error applying rules for eventId: {}", eventDto.getEventId(), e),
                        () -> log.info("Completed async ingestion for eventId: {}", eventDto.getEventId())
                );
    }

    private EventDto toEventDto(EventIngestionDto eventIngestionDto) {
        EventDto eventDto = new EventDto();
        eventDto.setEventId(UUID.fromString(eventIngestionDto.getEventId()));
        eventDto.setEventType(EventType.valueOf(eventIngestionDto.getEventType()));
        eventDto.setSourceApp(eventIngestionDto.getSourceApp());
        eventDto.setEventCreatedAt(eventIngestionDto.getEventCreatedAt());
        eventDto.setInitiatorId(eventIngestionDto.getInitiatorId());
        eventDto.setInitiatorType(eventIngestionDto.getInitiatorType());
        eventDto.setSeverity(eventIngestionDto.getSeverity());
        eventDto.setData(eventIngestionDto.getData());
        return eventDto;
    }
}
