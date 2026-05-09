package com.aerilon.turfclan.notification.service;

import com.aerilon.turfclan.web.dto.JoinWaitlistDto;

public interface SendEventToNotificationService {
    void sendEventToNotificationServiceForJoinWaitlist(JoinWaitlistDto  joinWaitlistDto, String emailId);
}
