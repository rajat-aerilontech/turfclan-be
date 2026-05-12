package com.aerilon.turfclan.generate_notification.listener;

import com.aerilon.turfclan.event_ingestion.entity.EventNotificationEntity;
import com.aerilon.turfclan.generate_notification.enums.Channel;
import com.aerilon.turfclan.generate_notification.service.EmailNotificationProcessorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Slf4j
@RequiredArgsConstructor
public class GenerateNotificationListener {

    private final EmailNotificationProcessorService emailNotificationProcessorService;

    @Async("notificationTaskExecutor")
    @EventListener
    public void onEventNotificationReceived(EventNotificationEntity event) {
        log.info("Processing event notification for eventId: {} with channel: {}", event.getEventId(), event.getNotificationChannel());
        Mono<Void> processMono;
        Channel channel = toChannel(event);
        if (channel == null) {
            return;
        }
        if (Channel.EMAIL.equals(channel)) {
            processMono = emailNotificationProcessorService.generateNotification(event);
        } else if (Channel.SMS.equals(channel)) {
            processMono = Mono.empty();
        } else {
            log.warn("Unsupported notification channel: {} for eventId: {}", channel, event.getEventId());
            processMono = Mono.empty();
        }
        processMono.subscribe(
                ignored -> {
                },
                e -> log.error("Error processing notification for eventId: {} with channel: {}", event.getEventId(), channel, e),
                () -> log.info("Completed notification processing for eventId: {} with channel: {}", event.getEventId(), channel)
        );
    }

    private Channel toChannel(EventNotificationEntity event) {
        try {
            return Channel.valueOf(event.getNotificationChannel());
        } catch (IllegalArgumentException | NullPointerException e) {
            log.warn("Unsupported notification channel: {} for eventId: {}", event.getNotificationChannel(), event.getEventId());
            return null;
        }
    }
}
