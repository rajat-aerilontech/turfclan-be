package com.aerilon.turfclan.partner.repository;

import com.aerilon.turfclan.partner.entity.OnboardingContractEntity;
import com.aerilon.turfclan.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OnboardingContractRepository extends JpaRepository<OnboardingContractEntity, UUID> {
    boolean existsByUser(UserEntity app);
    Optional<OnboardingContractEntity> findByUser(UserEntity user);
}
