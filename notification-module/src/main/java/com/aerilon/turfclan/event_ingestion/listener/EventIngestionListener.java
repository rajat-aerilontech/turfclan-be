package com.aerilon.turfclan.event_ingestion.listener;

import com.aerilon.turfclan.dto.EventIngestionDto;
import com.aerilon.turfclan.event_ingestion.dto.EventDto;
import com.aerilon.turfclan.event_ingestion.enums.EventType;
import com.aerilon.turfclan.event_ingestion.service.EventApplyRuleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventIngestionListener {

    private final EventApplyRuleService eventApplyRuleService;

    // @Async
    // @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
//    @EventListener
//    public Mono<Void> handleEventIngestionOnEventNotificationReceived(EventIngestedDto event) {
//        log.info("Processing ingestedEvent for eventId: {}", event.event());
//        eventApplyRuleService.applyRules(event.event())
//                .doOnError(e -> log.error("Error applying rules for eventId: {}", event.event(), e))
//                .subscribe();
//        return Mono.empty();
//    }

    @EventListener
    public void handleEventIngestionOnEventNotificationReceived(EventIngestionDto eventIngestionDto) {
        log.info("Processing ingestedEvent for eventId: {}", eventIngestionDto.getEventId());
        
        EventDto eventDto = new EventDto();
        eventDto.setEventId(UUID.fromString(eventIngestionDto.getEventId()));
        eventDto.setEventType(EventType.valueOf(eventIngestionDto.getEventType()));
        eventDto.setSourceApp(eventIngestionDto.getSourceApp());
        eventDto.setEventCreatedAt(eventIngestionDto.getEventCreatedAt());
        eventDto.setInitiatorId(eventIngestionDto.getInitiatorId());
        eventDto.setInitiatorType(eventIngestionDto.getInitiatorType());
        eventDto.setSeverity(eventIngestionDto.getSeverity());
        eventDto.setData(eventIngestionDto.getData());
        
        eventApplyRuleService.applyRules(eventDto)
                .doOnError(e -> log.error("Error applying rules for eventId: {}", eventDto.getEventId(), e))
                .block();
    }
}
