package com.aerilon.turfclan.web.service;


import com.aerilon.turfclan.web.dto.ContactInquiryDto;
import com.aerilon.turfclan.web.dto.JoinWaitlistDto;
import com.aerilon.turfclan.web.dto.PartnerWithUsDto;
import com.aerilon.turfclan.web.dto.WebResponseDto;

public interface WebsiteFormService {
    WebResponseDto createJoinWaitlist(JoinWaitlistDto request);
    WebResponseDto createPartnerWithUsQuery(PartnerWithUsDto request);
    WebResponseDto createContactInquiry(ContactInquiryDto request);
}
