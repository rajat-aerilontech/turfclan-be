package com.aerilon.turfclan.partner.repository;

import com.aerilon.turfclan.partner.entity.OnboardingApplicationEntity;
import com.aerilon.turfclan.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OnboardingApplicationRepository extends JpaRepository<OnboardingApplicationEntity, UUID> {
    Optional<OnboardingApplicationEntity> findByUser(UserEntity user);
}
