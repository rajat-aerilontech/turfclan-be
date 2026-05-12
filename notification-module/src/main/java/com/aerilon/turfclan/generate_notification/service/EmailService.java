package com.aerilon.turfclan.generate_notification.service;

import com.aerilon.turfclan.event_ingestion.entity.EventNotificationEntity;
import com.aerilon.turfclan.generate_notification.dto.NotificationRecipientInfoDto;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine emailTemplateEngine;
    private final EmailContentFactory emailContentFactory;

    @Value("${from.email.address}")
    private String fromEmail;

    @Value("${from.email.name}")
    private String fromName;

    public Mono<Void> sendEmail(String to, EventNotificationEntity event, NotificationRecipientInfoDto recipientInfoDto, String langCountryCode) {
        return Mono.fromCallable(() -> {
            log.info("Preparing to send email for event type {}", event.getEventType());
            EmailContentStrategy emailContentStrategy = emailContentFactory.getStrategy(event.getEventType());
            Context context = emailContentStrategy.buildContext(event, recipientInfoDto, langCountryCode);

            String subject = emailContentStrategy.buildSubject(event, context);
            String htmlContent = emailTemplateEngine.process(emailContentStrategy.getTemplateName(recipientInfoDto), context);

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom(fromEmail, fromName);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            mailSender.send(mimeMessage);
            log.info("{} sent to {} in locale {}", event.getEventType(), to, context.getLocale());
            return true;
        }).subscribeOn(Schedulers.boundedElastic())
        .doOnError(e -> log.error("Failed to send email for event type {} to {}: {}", event.getEventType(), to, e.getMessage(), e))
        .then();
    }
}
