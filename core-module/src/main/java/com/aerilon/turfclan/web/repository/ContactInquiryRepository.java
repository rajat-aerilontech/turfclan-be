package com.aerilon.turfclan.web.repository;

import com.aerilon.turfclan.web.entity.ContactInquiryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ContactInquiryRepository extends JpaRepository<ContactInquiryEntity, UUID> {
    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);
}
