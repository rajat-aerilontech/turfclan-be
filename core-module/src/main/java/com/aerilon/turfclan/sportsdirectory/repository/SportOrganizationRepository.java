package com.aerilon.turfclan.sportsdirectory.repository;

import com.aerilon.turfclan.sportsdirectory.entity.SportOrganizationEntity;
import com.aerilon.turfclan.sportsdirectory.enums.OrganizationType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface SportOrganizationRepository extends JpaRepository<SportOrganizationEntity, UUID> {

    // --- No Cursor (First Page) ---
    List<SportOrganizationEntity> findAllByOrderByCreatedAtDescIdDesc(Pageable pageable);
    List<SportOrganizationEntity> findByOrganizationTypeOrderByCreatedAtDescIdDesc(OrganizationType organizationType, Pageable pageable);

    // --- With Cursor (Subsequent Pages) ---
    @Query("""
        SELECT s FROM SportOrganizationEntity s 
        WHERE s.createdAt < :createdAt 
           OR (s.createdAt = :createdAt AND CAST(s.id AS string) < CAST(:id AS string)) 
        ORDER BY s.createdAt DESC, s.id DESC
    """)
    List<SportOrganizationEntity> findAllAfterCursor(
            @Param("createdAt") LocalDateTime createdAt,
            @Param("id") UUID id,
            Pageable pageable);

    @Query("""
        SELECT s FROM SportOrganizationEntity s 
        WHERE s.organizationType = :type 
          AND (s.createdAt < :createdAt OR (s.createdAt = :createdAt AND CAST(s.id AS string) < CAST(:id AS string))) 
        ORDER BY s.createdAt DESC, s.id DESC
    """)
    List<SportOrganizationEntity> findByOrganizationTypeAfterCursor(
            @Param("type") OrganizationType type,
            @Param("createdAt") LocalDateTime createdAt,
            @Param("id") UUID id,
            Pageable pageable);
}
