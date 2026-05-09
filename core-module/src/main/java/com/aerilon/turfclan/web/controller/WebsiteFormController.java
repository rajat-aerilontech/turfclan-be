package com.aerilon.turfclan.web.controller;

import com.aerilon.turfclan.exception.InvalidCaptchaException;
import com.aerilon.turfclan.service.RecaptchaService;
import com.aerilon.turfclan.web.dto.ContactInquiryDto;
import com.aerilon.turfclan.web.dto.JoinWaitlistDto;
import com.aerilon.turfclan.web.dto.PartnerWithUsDto;
import com.aerilon.turfclan.web.dto.WebResponseDto;
import com.aerilon.turfclan.web.service.WebsiteFormService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/web/form")
@RequiredArgsConstructor
@Tag(name = "Website Form Controller", description = "APIs for managing forms in the website")
public class WebsiteFormController {

    private final WebsiteFormService websiteFormService;
    private final RecaptchaService recaptchaService;

    @PostMapping("/join-waitlist")
    @PreAuthorize("hasAuthority('ROLE_TM_WEB')")
    @Operation(summary = "Join Waitlist", description = "Allows users to join the waitlist by providing either an email or a phone number. Validates the input and ensures that duplicate entries are not allowed.")
    public ResponseEntity<WebResponseDto> handleJoinWaitlist(
            @RequestBody @Valid JoinWaitlistDto request) {
        if (!recaptchaService.isVerify(request.getRecaptchaToken())) {
            throw new InvalidCaptchaException("Captcha verification failed");
        }
        return ResponseEntity.ok(websiteFormService.createJoinWaitlist(request));
    }

    @PostMapping("/partner-with-us-query")
    @PreAuthorize("hasAuthority('ROLE_TM_WEB')")
    @Operation(summary = "Partner With Us Query", description = "Allows potential partners to submit their queries by providing their name, brand name, contact information (email or phone), and a description of their query. Validates the input and ensures that duplicate entries are not allowed.")
    public ResponseEntity<WebResponseDto> handlePartnerWithUsQuery(
            @RequestBody @Valid PartnerWithUsDto request) {
//        if (!recaptchaService.isVerify(request.getRecaptchaToken())) {
//            throw new InvalidCaptchaException("Captcha verification failed");
//        }
        return ResponseEntity.ok(websiteFormService.createPartnerWithUsQuery(request));
    }

    @PostMapping("/contact-us-query")
    @PreAuthorize("hasAuthority('ROLE_TM_WEB')")
    @Operation(summary = "Contact Us Query", description = "Generates and sends an OTP to a given phone number and confirms that the OTP was sent successfully.")
    public ResponseEntity<WebResponseDto> handleContactUsQuery(
            @RequestBody @Valid ContactInquiryDto request) {
//        if (!recaptchaService.isVerify(request.getRecaptchaToken())) {
//            throw new InvalidCaptchaException("Captcha verification failed");
//        }
        return ResponseEntity.ok(websiteFormService.createContactInquiry(request));
    }
}
