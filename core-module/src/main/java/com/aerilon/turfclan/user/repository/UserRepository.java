package com.aerilon.turfclan.user.repository;

import com.aerilon.turfclan.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, UUID> {
    Optional<UserEntity> findByUserEmail(String userEmail);
    Optional<UserEntity> findByPhoneNumber(String phoneNumber);
    List<UserEntity> findByUserRoleIn(List<String> roles);
}

