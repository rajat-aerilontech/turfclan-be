package com.aerilon.turfclan.web.repository;

import com.aerilon.turfclan.web.entity.JoinWaitlistEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface JoinWaitlistRepository extends JpaRepository<JoinWaitlistEntity, UUID> {
    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);
}
