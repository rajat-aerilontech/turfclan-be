package com.aerilon.turfclan.notification.service;

import com.aerilon.turfclan.web.dto.JoinWaitlistDto;

public interface SendEventToNotificationService {
    void sendEventToNotificationServiceForJoinWaitlist(JoinWaitlistDto  joinWaitlistDto, String emailId);
    void sendEventToNotificationServiceForPartnerWithUsQuery(com.aerilon.turfclan.web.dto.PartnerWithUsDto partnerWithUsDto, String input);
    void sendEventToNotificationServiceForContactInquiry(com.aerilon.turfclan.web.dto.ContactInquiryDto contactInquiryDto, String input);
}
