package com.aerilon.turfclan.sportsdirectory.repository;

import com.aerilon.turfclan.sportsdirectory.entity.SportClubEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SportClubRepository extends JpaRepository<SportClubEntity, UUID> {

    Optional<SportClubEntity> findFirstByShortNameIgnoreCase(String shortName);

    Optional<SportClubEntity> findFirstByNameIgnoreCase(String name);
}