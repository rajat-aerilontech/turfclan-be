package com.aerilon.turfclan.event_ingestion.service.impl;

import com.aerilon.turfclan.event_ingestion.dto.ChannelResult;
import com.aerilon.turfclan.event_ingestion.dto.EventDto;
import com.aerilon.turfclan.event_ingestion.dto.RuleSets;
import com.aerilon.turfclan.event_ingestion.entity.EventNotificationEntity;
import com.aerilon.turfclan.event_ingestion.enums.EventStatus;
import com.aerilon.turfclan.event_ingestion.enums.EventType;
import com.aerilon.turfclan.event_ingestion.mapper.RuleMapper;
import com.aerilon.turfclan.event_ingestion.repository.EventNotificationRepository;
import com.aerilon.turfclan.event_ingestion.repository.RuleRepository;
import com.aerilon.turfclan.event_ingestion.service.EventApplyRuleService;
import com.aerilon.turfclan.utils.JsonMapUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class EventApplyRuleServiceImpl implements EventApplyRuleService {

    private final RuleMapper ruleMapper;
    private final RuleRepository ruleRepository;
    private final EventNotificationRepository eventNotificationRepository;
    private final ApplicationEventPublisher eventNotifyToGenerateNotifyPublisher;

    @Override
    public Mono<Void> applyRules(EventDto event) {
        log.info("Applying rules for eventId: {}", event.getEventId());
        return Mono.fromCallable(() -> ruleRepository.findBySourceAppAndEventType(
                        event.getSourceApp(),
                        event.getEventType()
                ))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(rule -> rule == null ? Mono.empty() : Mono.just(rule))
                .switchIfEmpty(Mono.defer(() -> {
                    log.warn("No rules found for eventId: {} and source: {} and event: {}",
                            event.getEventId(), event.getSourceApp(), event.getEventType());
                    return updateEventStatusAndBatchCounter(event, Collections.emptyList(), EventStatus.FAILED)
                            .then(Mono.empty());
                }))
                .map(ruleMapper::toDto)
                .doOnNext(ruleDto -> log.debug("Rules found for eventId: {} | rules: {}",
                        event.getEventId(), ruleDto.getRuleSets()))
                .flatMap(ruleDto -> processEventWithRule(event, ruleDto.getRuleSets()))
                .onErrorResume(e -> {
                    log.error("Rule processing failed | eventId: {} | error: {}",
                            event.getEventId(), e.getMessage(), e);

                    return updateEventStatusAndBatchCounter(event, Collections.emptyList(), EventStatus.FAILED);
                })
                .then();
    }

    protected Mono<Void> processEventWithRule(EventDto event, RuleSets ruleSets) {
        List<String> channelToProcess = (event.getFailedChannel() != null && !event.getFailedChannel().isEmpty()) ? event.getFailedChannel() : ruleSets.getChannel();
        log.info("EventId {} will be processed for channel: {}", event.getEventId(), channelToProcess);
        return Flux.fromIterable(channelToProcess)
                .flatMap(channel -> {
                    log.debug("Processing event: {} with channel: {}",  event.getEventId(), channel);
                    return populateEventNotificationData(event, ruleSets, channel)
                            .flatMap(entity ->
                                    Mono.fromCallable(() -> eventNotificationRepository.save(entity))
                                            .subscribeOn(Schedulers.boundedElastic())
                                            .doOnSuccess(saved -> {
                                                log.debug("Successfully saved notification | eventId: {} | channel: {}",
                                                        event.getEventId(), channel);
                                                eventNotifyToGenerateNotifyPublisher.publishEvent(saved);
                                            })
                            )
                            .thenReturn(new ChannelResult(channel, true))
                            .onErrorResume(e -> {
                                log.error("Error processing eventId: {} for channel: {}", event.getEventId(), channel);
                                return Mono.just(new ChannelResult(channel, false));
                            });
                })
                .collectList()
                .flatMap(results -> {
                   var failedChannels = results.stream()
                           .filter(result -> !result.success())
                           .map(ChannelResult::channel)
                           .toList();
                   EventStatus status = failedChannels.isEmpty() ? EventStatus.PUBLISHED : EventStatus.FAILED;
                    if (failedChannels.isEmpty()) {
                        log.info("Successfully processed all channels | eventId: {} | status: {}",
                                event.getEventId(), status);
                    } else {
                        log.warn("Processing completed with failures | eventId: {} | failedChannels: {} | status: {}",
                                event.getEventId(), failedChannels, status);
                    }
                   return updateEventStatusAndBatchCounter(event, failedChannels, status);
                });
    }

    protected Mono<Void> updateEventStatusAndBatchCounter(EventDto event, List<String> channels, EventStatus status) {
        log.debug("Updating event status for eventId: {}. Failed channels: {}, Setting status to: {}", event.getEventId(), channels, status);
        return Mono.defer(() -> {
            if (EventStatus.FAILED.equals(status)) {
                event.setFailedChannel(channels);
            } else {
                event.setFailedChannel(Collections.emptyList());
            }
            return Mono.empty();
        });
    }

    protected Mono<EventNotificationEntity> populateEventNotificationData(EventDto event, RuleSets ruleSets, String channel) {
        Map<String, Object> objectMap = new HashMap<>(event.getData());
        Map<String, Object> audienceMetaMap = new HashMap<>();
        audienceMetaMap.put("initiatorId", event.getInitiatorId());
        return Mono.defer(() -> {
            EventNotificationEntity entity = new EventNotificationEntity();
            entity.setEventId(event.getEventId());
            entity.setEventNotificationId(UUID.randomUUID());
            entity.setEventCreatedAt(event.getEventCreatedAt());
            entity.setEventType(event.getEventType());
            entity.setSourceApp(event.getSourceApp());
            entity.setData(JsonMapUtil.mapToJson(objectMap));
            entity.setEventCreatedBy(event.getInitiatorId());
            entity.setCreatedAt(LocalDateTime.now());
            entity.setTitle(ruleSets.getNotification().getTitle());
            entity.setBody(ruleSets.getNotification().getBody());
            entity.setNotificationCategory(ruleSets.getCategory());
            entity.setNotificationChannel(channel);
            entity.setNotificationDisplayType(ruleSets.getDisplayType());
            entity.setNotificationSeverity(ruleSets.getSeverity());
            entity.setRuleName(ruleSets.getName());
            entity.setIncludeInitiator(ruleSets.getAudience().getIncludeInitiator());
            entity.setTargetAudience(ruleSets.getAudience().getTargets().toArray(new String[0]));
            entity.setStatus(EventStatus.STAGED);
            return frameDynamicTextAndUrl(entity, event, ruleSets);
        });
    }

    private Mono<EventNotificationEntity> frameDynamicTextAndUrl(EventNotificationEntity entity, EventDto event, RuleSets ruleSets) {
        return Mono.defer(() ->{
            EventType eventType = event.getEventType();
            if(EventType.CONTACT_US_QUERY.equals(eventType)){
                return Mono.just(entity);
            } else if (EventType.JOIN_WAITLIST.equals(eventType)) {
                return Mono.just(entity);
            } else if (EventType.PARTNER_QUERY.equals(eventType)) {
                return Mono.just(entity);
            } else {
                entity.setBody(ruleSets.getNotification().getBody());
                entity.setActionUrl(ruleSets.getNotification().getActionUrl());
                entity.setActionType(ruleSets.getNotification().getActionType());
                entity.setActionName(ruleSets.getNotification().getActionName());
                return Mono.just(entity);
            }
        });
    }
}
