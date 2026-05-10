package com.aerilon.turfclan.generate_notification.listener;

import com.aerilon.turfclan.event_ingestion.entity.EventNotificationEntity;
import com.aerilon.turfclan.generate_notification.enums.Channel;
import com.aerilon.turfclan.generate_notification.service.EmailNotificationProcessorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Slf4j
@RequiredArgsConstructor
public class GenerateNotificationListener {

    private final EmailNotificationProcessorService emailNotificationProcessorService;

    @EventListener
    public void onEventNotificationReceived(EventNotificationEntity event) {
        log.info("Processing event notification for eventId: {} with channel: {}", event.getEventId(), event.getNotificationChannel());
        Mono<Void> processMono;
        String channel = event.getNotificationChannel();
        if (Channel.EMAIL.equals(Channel.valueOf(channel))) {
            processMono = emailNotificationProcessorService.generateNotification(event);
        } else if (Channel.SMS.equals(Channel.valueOf(channel))) {
            processMono = Mono.empty();
        } else {
            log.warn("Unsupported notification channel: {} for eventId: {}", channel, event.getEventId());
            processMono = Mono.empty();
        }
        processMono.doOnError(e -> log.error("Error processing notification for eventId: {} with channel: {}", event.getEventId(), channel, e))
                .subscribe();
    }
}
