package com.aerilon.turfclan.sportsdirectory.repository;

import com.aerilon.turfclan.sportsdirectory.entity.SportAssociationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SportAssociationRepository extends JpaRepository<SportAssociationEntity, UUID> {

    List<SportAssociationEntity> findBySelectedSportExperienceIgnoreCase(String selectedSportExperience);

    Optional<SportAssociationEntity> findByIdAndSelectedSportExperienceIgnoreCase(UUID id, String selectedSportExperience);

    Optional<SportAssociationEntity> findFirstByShortNameIgnoreCaseAndSelectedSportExperienceIgnoreCase(String shortName, String selectedSportExperience);

    Optional<SportAssociationEntity> findFirstByNameIgnoreCaseAndSelectedSportExperienceIgnoreCase(String name, String selectedSportExperience);

    Optional<SportAssociationEntity> findFirstByShortNameIgnoreCase(String shortName);

    Optional<SportAssociationEntity> findFirstByNameIgnoreCase(String name);
}
