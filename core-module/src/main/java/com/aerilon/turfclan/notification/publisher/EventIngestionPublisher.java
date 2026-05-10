package com.aerilon.turfclan.notification.publisher;

import com.aerilon.turfclan.dto.EventIngestionDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventIngestionPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    public void publish(EventIngestionDto eventIngestionDto) {
        log.info("Publishing EventIngestionDto for event: {}", eventIngestionDto.getEventId());
        applicationEventPublisher.publishEvent(eventIngestionDto);
    }
}