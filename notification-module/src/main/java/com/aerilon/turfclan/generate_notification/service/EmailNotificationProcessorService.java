package com.aerilon.turfclan.generate_notification.service;

import com.aerilon.turfclan.event_ingestion.entity.EventNotificationEntity;
import com.aerilon.turfclan.event_ingestion.entity.NotificationEmailEntity;
import com.aerilon.turfclan.event_ingestion.enums.EventStatus;
import com.aerilon.turfclan.event_ingestion.mapper.EventNotifyToEmailNotifyEntityMapper;
import com.aerilon.turfclan.event_ingestion.repository.NotificationEmailRepository;
import com.aerilon.turfclan.generate_notification.dto.NotificationRecipientInfoDto;
import com.aerilon.turfclan.generate_notification.dto.RecipientResult;
import com.aerilon.turfclan.generate_notification.enums.Channel;
import com.aerilon.turfclan.utils.JsonMapUtil;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailNotificationProcessorService {

    private final EmailService emailService;
    private final NotificationProcessorHelper processorHelper;
    private final NotificationEmailRepository notificationEmailRepository;
    private final EventNotifyToEmailNotifyEntityMapper emailEntityMapper;

     public Mono<Void> generateNotification(EventNotificationEntity event) {
         Mono<Map<String, NotificationRecipientInfoDto>> notificationRecipientsMap = processorHelper.loadAndFilterRecipients(event, Channel.EMAIL).doOnNext(map -> log.info("Recipients Map: {}", map));
         return notificationRecipientsMap
                 .flatMapMany(map -> Flux.fromIterable(map.entrySet()).onBackpressureBuffer())
                 .flatMap(entry -> {
                     Mono<NotificationEmailEntity> emailEntityMono = Mono.fromCallable(() -> emailEntityMapper.toEntity(event));
                     return handleSingleRecipient(event, entry, emailEntityMono);
                 })
                 .collectList()
                 .flatMap(results -> processorHelper.aggregateAndUpdateStatus(results, event, Channel.EMAIL))
                 .onErrorResume(e -> {
                     log.error("Error in processing notifications for eventId: {}", event.getEventId(), e);
                     return processorHelper.updateEventNotificationStatusAndBatchCounter(
                             EventStatus.FAILED.name(),
                             event.getBatchCounter(),
                             List.of(),
                             event.getEventId(),
                             Channel.EMAIL
                     );
                 });
     }

     private Mono<RecipientResult> handleSingleRecipient(EventNotificationEntity event,
                                                         Map.Entry<String, NotificationRecipientInfoDto> entry,
                                                         Mono<NotificationEmailEntity> notificationEntity) {
         String email = entry.getKey();
         NotificationRecipientInfoDto notificationRecipientInfoDto = entry.getValue();
         return notificationEntity
                 .flatMap(entity -> sendEmail(event, email, notificationRecipientInfoDto)
                         .then(createEmailNotification(entity, email, notificationRecipientInfoDto))
                         .thenReturn(new RecipientResult(email, true))
         )
         .onErrorResume(e -> {
             log.error("Error processing notification for email: {} | eventId: {}", email, event.getEventId(), e);
             return Mono.just(new RecipientResult(email, false));
         });
     }

     private Mono<Void> sendEmail(EventNotificationEntity event, String email, NotificationRecipientInfoDto recipientInfoDto) {
         if(event.getNotificationChannel().contains(Channel.EMAIL.name())) {
             String langCountryCode = recipientInfoDto.getLanguageIsoCode() + "_" + recipientInfoDto.getLanguageIsoCode();
             return emailService.sendEmail(email, event, recipientInfoDto, langCountryCode);
         } else {
             log.debug("Email channel is not enabled for the event : {}", event.getEventId());
             return Mono.empty();
         }
     }

     private Mono<Void> createEmailNotification(NotificationEmailEntity notificationEmailEntity,
                                                String email,
                                                NotificationRecipientInfoDto recipientInfoDto) {
         return setDynamicUrlLangCode(notificationEmailEntity, recipientInfoDto)
                 .flatMap(updatedEntity -> {
                     updatedEntity.setCreatedAt(LocalDateTime.now());
                     updatedEntity.setUserEmail(email);
                     updatedEntity.setCountryCode(recipientInfoDto.getCountryIsoCode());
                     return Mono.fromRunnable(() -> {
                         notificationEmailRepository.save(updatedEntity);
                         log.info("Email notification saved | email: {} | eventId: {}",
                                 email, updatedEntity.getEventId());
                     }).subscribeOn(Schedulers.boundedElastic());
                 }).then();
     }

     private Mono<NotificationEmailEntity> setDynamicUrlLangCode(NotificationEmailEntity entity,
                                                                 NotificationRecipientInfoDto recipientInfoDto) {
         return Mono.fromSupplier(() -> {
             String langCode = recipientInfoDto.getLanguageIsoCode() + "_" + recipientInfoDto.getCountryIsoCode();
             try {
                 if (entity.getNotificationAction() == null) {
                     log.debug("Notification action is null for entity: {}", entity);
                     return entity;
                 }
                 String notificationActionStr = entity.getNotificationAction();
                 var notificationActionNode = JsonMapUtil.toJsonNode(notificationActionStr);
                 String actionUrlTemplate = notificationActionNode.get("url").asText();
                 String actionUrl = actionUrlTemplate.replace("{langCode}", langCode);
                 ((ObjectNode) notificationActionNode).put("url", actionUrl);
                 entity.setNotificationAction(notificationActionNode.toString());
             } catch (Exception e) {
                 log.error("Failed to update URL in notification Action", e);
             }
             return entity;
         }).subscribeOn(Schedulers.boundedElastic());
     }
}
