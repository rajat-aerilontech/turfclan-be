package com.aerilon.turfclan.partner.repository;

import com.aerilon.turfclan.partner.entity.BrandDetailEntity;
import com.aerilon.turfclan.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface BrandDetailRepository extends JpaRepository<BrandDetailEntity, UUID> {
    BrandDetailEntity findByUser(UserEntity user);
}
