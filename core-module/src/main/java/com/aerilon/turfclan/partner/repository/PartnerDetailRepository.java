package com.aerilon.turfclan.partner.repository;

import com.aerilon.turfclan.partner.entity.PartnerDetailEntity;
import com.aerilon.turfclan.user.entity.UserEntity;
import jakarta.servlet.http.Part;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PartnerDetailRepository extends JpaRepository<PartnerDetailEntity, UUID> {
    boolean existsByUser(UserEntity app);
    Optional<PartnerDetailEntity> findByUser(UserEntity user);
}
