package com.aerilon.turfclan.partner.repository;

import com.aerilon.turfclan.partner.entity.SocialLinkEntity;
import com.aerilon.turfclan.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SocialLinkRepository extends JpaRepository<SocialLinkEntity, UUID> {
    List<SocialLinkEntity> findByUser(UserEntity user);
}
