package com.aerilon.turfclan.partner.repository;

import com.aerilon.turfclan.partner.entity.PartnerDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PartnerDetailRepository extends JpaRepository<PartnerDetailEntity, UUID> {
}
