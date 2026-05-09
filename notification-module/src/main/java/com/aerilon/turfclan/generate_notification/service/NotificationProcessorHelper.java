package com.aerilon.turfclan.generate_notification.service;

import com.aerilon.turfclan.dto.UserDto;
import com.aerilon.turfclan.event_ingestion.entity.EventNotificationEntity;
import com.aerilon.turfclan.event_ingestion.enums.EventStatus;
import com.aerilon.turfclan.event_ingestion.repository.EventNotificationRepository;
import com.aerilon.turfclan.generate_notification.dto.NotificationRecipientInfoDto;
import com.aerilon.turfclan.generate_notification.dto.RecipientResult;
import com.aerilon.turfclan.generate_notification.enums.Channel;
import com.aerilon.turfclan.service.UserLookUpService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class NotificationProcessorHelper {

    private final UserLookUpService userLookupService;
    private final EventNotificationRepository eventNotificationRepository;

    public Mono<Map<String, NotificationRecipientInfoDto>> loadAndFilterRecipients(EventNotificationEntity event, Channel  channel) {
        return getNotificationRecipientsMap(event)
                .doOnNext(initial -> log.debug("Initial recipients map for eventId {}: {}", event.getEventId(), initial))
                .map(map -> filterRecipientsByFailure(event, map))
                .doOnNext(afterRetry -> log.debug("Recipients map after retry filtering for eventId {}: {}", event.getEventId(), afterRetry));
    }

    public Mono<Map<String, NotificationRecipientInfoDto>> getNotificationRecipientsMap(EventNotificationEntity event) {
        String category = event.getNotificationCategory() != null ? event.getNotificationCategory().name() : "";
        if("subscription".equalsIgnoreCase(category)) {
            return getRecipientsForGeneralFormOrSpecialEvent(event)
                    .doOnNext(map -> log.info("Successfully generated recipients map for eventId: {} | Map content: {}",
                            event.getEventId(), map));
        }
        return Mono.just(Map.<String, NotificationRecipientInfoDto>of())
                .doOnNext(map -> log.debug("Category not 'subscription', returning empty map for eventId: {}",
                        event.getEventId()));
    }
    public Map<String, NotificationRecipientInfoDto> filterRecipientsByFailure(EventNotificationEntity event, Map<String, NotificationRecipientInfoDto> allRecipients) {
        String[] failed = event.getFailedRecipients();
        if (failed == null || failed.length == 0) {
            return allRecipients;
        }
        Set<String> failedSet = new HashSet<>(Arrays.asList(failed));
        Map<String, NotificationRecipientInfoDto> filtered = allRecipients.entrySet().stream()
                .filter(e -> failedSet.contains(e.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        log.info("Filtered recipients for only failed recipients {}: eventId{}", failedSet, event.getEventId());
        return filtered;
    }

    private Mono<Map<String, NotificationRecipientInfoDto>> getRecipientsForGeneralFormOrSpecialEvent(EventNotificationEntity event) {
        String[] targets = event.getTargetAudience();
        Boolean includeInitiator = event.getIncludeInitiator();
        String initiatorEmail = event.getEventCreatedBy();
        Mono<Map<String, NotificationRecipientInfoDto>> dbRecipientsMono;
        if (targets != null && targets.length > 0) {
            List<String> roles = Arrays.asList(targets);
            dbRecipientsMono = Mono.fromCallable(() -> userLookupService.findByRoles(roles))
                    .subscribeOn(Schedulers.boundedElastic())
                    .map(users -> users.stream()
                            .filter(user -> user.getUserEmail() != null && !user.getUserEmail().trim().isEmpty())
                            .filter(user -> shouldIncludeUser(user, initiatorEmail, includeInitiator))
                            .collect(Collectors.toMap(
                                    UserDto::getUserEmail,
                                    this::mapToRecipientDto,
                                    (existing, replacement) -> existing
                            )));
        } else {
            dbRecipientsMono = Mono.just(new HashMap<>());
        }
        return dbRecipientsMono.map(dbRecipients -> {
            Map<String, NotificationRecipientInfoDto> finalRecipients = new HashMap<>(dbRecipients);
            if (Boolean.TRUE.equals(includeInitiator) && initiatorEmail != null && !initiatorEmail.trim().isEmpty()) {
                if (!finalRecipients.containsKey(initiatorEmail)) {
                    NotificationRecipientInfoDto initiatorDto = new NotificationRecipientInfoDto();
                    initiatorDto.setUserEmail(initiatorEmail);
                    initiatorDto.setUserRole("INITIATOR");
                    initiatorDto.setLanguageIsoCode("en");
                    initiatorDto.setCountryIsoCode("US");
                    finalRecipients.put(initiatorEmail, initiatorDto);
                }
            }
            return finalRecipients;
        });
    }

    private boolean shouldIncludeUser(UserDto user, String initiatorEmail, Boolean includeInitiator) {
        if (Boolean.FALSE.equals(includeInitiator)) {
            return user.getUserEmail() != null && !user.getUserEmail().equalsIgnoreCase(initiatorEmail);
        }
        return true;
    }

    private NotificationRecipientInfoDto mapToRecipientDto(UserDto user) {
        NotificationRecipientInfoDto dto = new NotificationRecipientInfoDto();
        dto.setUserEmail(user.getUserEmail());
        dto.setUserRole(user.getUserRole());
        dto.setLanguageIsoCode(user.getLanguageIsoCode());
        dto.setCountryIsoCode(user.getCountryIsoCode());
        return dto;
    }

    public Mono<Void> aggregateAndUpdateStatus(List<RecipientResult> results, EventNotificationEntity event, Channel  channel) {
        List<String> failedRecipients = results.stream()
                .filter(r -> !r.success())
                .map(RecipientResult::recipient)
                .toList();

        String status = failedRecipients.isEmpty()
                ? EventStatus.PUBLISHED.name()
                : EventStatus.FAILED.name();

        return updateEventNotificationStatusAndBatchCounter(
                status,
                event.getBatchCounter(),
                failedRecipients,
                event.getEventId(),
                channel
        );
    }

    public Mono<Void> updateEventNotificationStatusAndBatchCounter(String eventStatus, int batchCounter, List<String> recipients, UUID eventId, Channel channel) {
        String[] failedArr = recipients == null ? new String[0] : recipients.toArray(String[]::new);
        return Mono.fromCallable(() -> {
            int updatedRows = eventNotificationRepository.updateStatusAndBatchCounter(
                    EventStatus.valueOf(eventStatus),
                    failedArr,
                    batchCounter,
                    eventId,
                    channel.name()
            );
            if (updatedRows == 0) {
                log.warn("No rows updated for eventId: {} and channel: {}", eventId, channel);
            } else {
                log.info("Updated {} row(s) for eventId: {} and channel: {}", updatedRows, eventId, channel);
            }
            return updatedRows;
        }).subscribeOn(Schedulers.boundedElastic()).then();
    }
}
