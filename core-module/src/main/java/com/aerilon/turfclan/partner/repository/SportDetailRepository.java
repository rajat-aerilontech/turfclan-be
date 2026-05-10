package com.aerilon.turfclan.partner.repository;

import com.aerilon.turfclan.facility.entity.SubFacilityEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SportDetailRepository extends JpaRepository<SubFacilityEntity, UUID> {
}
