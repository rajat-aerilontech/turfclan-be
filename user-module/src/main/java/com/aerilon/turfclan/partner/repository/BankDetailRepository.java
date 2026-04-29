package com.aerilon.turfclan.partner.repository;

import com.aerilon.turfclan.partner.entity.BankDetailEntity;
import com.aerilon.turfclan.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BankDetailRepository extends JpaRepository<BankDetailEntity, UUID> {
    boolean existsByUser(UserEntity app);
    Optional<BankDetailEntity> findByUser(UserEntity user);
}
