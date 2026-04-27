package com.aerilon.turfclan.partner.repository;

import com.aerilon.turfclan.partner.entity.BusinessDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface BusinessDetailRepository extends JpaRepository<BusinessDetailEntity, UUID> {
}
