package com.aerilon.turfclan.web.repository;

import com.aerilon.turfclan.web.entity.PartnerWithUsQueryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PartnerWithUsQueryRepository extends JpaRepository<PartnerWithUsQueryEntity, UUID> {
    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phone);
}
