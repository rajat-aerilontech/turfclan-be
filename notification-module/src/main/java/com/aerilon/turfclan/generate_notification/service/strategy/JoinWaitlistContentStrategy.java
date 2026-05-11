package com.aerilon.turfclan.generate_notification.service.strategy;

import com.aerilon.turfclan.event_ingestion.entity.EventNotificationEntity;
import com.aerilon.turfclan.event_ingestion.enums.EventType;
import com.aerilon.turfclan.generate_notification.dto.NotificationRecipientInfoDto;
import com.aerilon.turfclan.generate_notification.service.EmailContentStrategy;
import com.aerilon.turfclan.utils.JsonMapUtil;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;

import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

@Component
public class JoinWaitlistContentStrategy implements EmailContentStrategy {

    private static final String BASE_EMAIL_PROPERTY_NAME = "templates/emailProperties/joinWaitlist";
    private static final String SUBJECT_KEY = "email.joinWaitlist.subject";
    private static final String TEMPLATE_NAME = "joinWaitlistEmail";
    private static final String ADMIN_BASE_EMAIL_PROPERTY_NAME = "templates/emailProperties/joinWaitlistAdmin";
    private static final String ADMIN_SUBJECT_KEY = "email.joinWaitlist.admin.subject";
    private static final String ADMIN_TEMPLATE_NAME = "admin/joinWaitlistAdminEmail";
    private static final String ADMIN_PLACEHOLDER = "ADMIN";

    @Override
    public String getTemplateName(NotificationRecipientInfoDto recipientInfo) {
        if (recipientInfo != null && ADMIN_PLACEHOLDER.equalsIgnoreCase(recipientInfo.getUserRole())) {
            return ADMIN_TEMPLATE_NAME;
        }
        return TEMPLATE_NAME;
    }

    @Override
    public EventType getEventType() { return EventType.JOIN_WAITLIST; }

    @Override
    public Context buildContext(EventNotificationEntity event, NotificationRecipientInfoDto recipientInfo, String langCountryCode) {
        Locale locale = new Locale(
                recipientInfo.getLanguageIsoCode(),
                recipientInfo.getCountryIsoCode()
        );
        Context context = new Context(locale);
        Map<String, Object> eventData = JsonMapUtil.jsonToMap(event.getData());
        context.setVariables(eventData);
        if (!eventData.containsKey("userName") && event.getEventCreatedBy() != null) {
            context.setVariable("userName", event.getEventCreatedBy());
        }
        context.setVariable("initiatorId", event.getEventCreatedBy());
        context.setVariable("userRole", recipientInfo.getUserRole());
        return context;
    }

    @Override
    public String buildSubject(EventNotificationEntity event, Context context) {
        Locale locale = context.getLocale();
        String userRole = (String) context.getVariable("userRole");
        if (ADMIN_PLACEHOLDER.equalsIgnoreCase(userRole)) {
            ResourceBundle adminBundle = ResourceBundle.getBundle(ADMIN_BASE_EMAIL_PROPERTY_NAME, locale);
            String subject = adminBundle.getString(ADMIN_SUBJECT_KEY);
            return java.text.MessageFormat.format(subject, context.getVariable("userName"));
        }
        ResourceBundle resourceBundle = ResourceBundle.getBundle(BASE_EMAIL_PROPERTY_NAME, locale);
        return String.format("%s", resourceBundle.getString(SUBJECT_KEY));
    }
}
