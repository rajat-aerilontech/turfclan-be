package com.aerilon.turfclan.user.repository;

import com.aerilon.turfclan.user.entity.UserSportAssociationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserSportAssociationRepository extends JpaRepository<UserSportAssociationEntity, UUID> {
    Optional<UserSportAssociationEntity> findByUserId(UUID userId);
}
