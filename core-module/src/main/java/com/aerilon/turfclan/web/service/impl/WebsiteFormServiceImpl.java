package com.aerilon.turfclan.web.service.impl;


import com.aerilon.turfclan.exception.ResourceAlreadyExistsException;
import com.aerilon.turfclan.web.dto.ContactInquiryDto;
import com.aerilon.turfclan.web.dto.JoinWaitlistDto;
import com.aerilon.turfclan.web.dto.PartnerWithUsDto;
import com.aerilon.turfclan.web.dto.WebResponseDto;
import com.aerilon.turfclan.web.entity.ContactInquiryEntity;
import com.aerilon.turfclan.web.entity.JoinWaitlistEntity;
import com.aerilon.turfclan.web.entity.PartnerWithUsQueryEntity;
import com.aerilon.turfclan.web.repository.ContactInquiryRepository;
import com.aerilon.turfclan.web.repository.JoinWaitlistRepository;
import com.aerilon.turfclan.web.repository.PartnerWithUsQueryRepository;
import com.aerilon.turfclan.web.service.WebsiteFormService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class WebsiteFormServiceImpl implements WebsiteFormService {

    private final JoinWaitlistRepository joinWaitlistRepository;
    private final PartnerWithUsQueryRepository partnerWithUsQueryRepository;
    private final ContactInquiryRepository contactInquiryRepository;

    @Override
    public WebResponseDto createJoinWaitlist(JoinWaitlistDto request) {
        log.info("Processing join waitlist request");
        String input = request.getEmail_phone().trim();
        JoinWaitlistEntity entity = new JoinWaitlistEntity();
        boolean exists;
        if (input.contains("@")) {
            String email = input.toLowerCase();
            exists = joinWaitlistRepository.existsByEmail(email);
            entity.setEmail(email);
        } else {
            exists = joinWaitlistRepository.existsByPhoneNumber(input);
            entity.setPhoneNumber(input);
        }
        if (exists) {
            log.warn("Waitlist duplicate attempt");
            throw new ResourceAlreadyExistsException("You have already joined our waitlist!");
        }
        joinWaitlistRepository.save(entity);
        log.info("Successfully added to waitlist");
        return WebResponseDto.builder()
                .status("success")
                .message("Join waitlist request received successfully.")
                .build();
    }

    @Override
    public WebResponseDto createPartnerWithUsQuery(PartnerWithUsDto request) {
        log.info("Processing partner with us request for: {}", request.getEmail_phone());

        String input = request.getEmail_phone().trim();
        PartnerWithUsQueryEntity entity = new PartnerWithUsQueryEntity();
        entity.setName(request.getName());
        entity.setBrandName(request.getBrandName());
        entity.setDescription(request.getDescription());
        boolean alreadyExists;
        if (input.contains("@")) {
            alreadyExists = partnerWithUsQueryRepository.existsByEmail(input);
            entity.setEmail(input);
        } else {
            alreadyExists = partnerWithUsQueryRepository.existsByPhoneNumber(input);
            entity.setPhoneNumber(input);
        }
        if (alreadyExists) {
            throw new ResourceAlreadyExistsException("This email or phone number is already registered.");
        }
        partnerWithUsQueryRepository.save(entity);
        log.info("Partner query created successfully");
        return WebResponseDto.builder()
                .status("success")
                .message("Partner inquiry submitted successfully.")
                .build();
    }

    @Override
    public WebResponseDto createContactInquiry(ContactInquiryDto request) {
        log.info("Processing contact inquiry via: {}", request.getContactBy());
        ContactInquiryEntity entity = new ContactInquiryEntity();
        entity.setName(request.getName());
        entity.setContactBy(request.getContactBy().toUpperCase());
        entity.setEmail(request.getEmail());
        entity.setPhoneNumber(request.getPhoneNumber());
        entity.setSubject(request.getSubject());
        entity.setDescription(request.getDescription());
        contactInquiryRepository.save(entity);
        log.info("Contact inquiry created successfully for {}", request.getName());
        return WebResponseDto.builder()
                .status("success")
                .message("Your inquiry has been sent successfully.")
                .build();
    }
}
