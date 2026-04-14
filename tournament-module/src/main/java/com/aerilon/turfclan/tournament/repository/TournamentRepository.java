package com.aerilon.turfclan.tournament.repository;

import com.aerilon.turfclan.tournament.entity.TournamentEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TournamentRepository extends JpaRepository<TournamentEntity, UUID> {

    @EntityGraph(attributePaths = "admin")
        List<TournamentEntity> findBySportCategoryIgnoreCaseOrderByCreatedAtDescIdDesc(String sportCategory,
                                                                                                                                                                     Pageable pageable);

        @EntityGraph(attributePaths = "admin")
        @Query("""
                        select tournament
                        from TournamentEntity tournament
                        where lower(tournament.sportCategory) = lower(:sportCategory)
                            and (
                                        tournament.createdAt < :cursorCreatedAt
                                        or (tournament.createdAt = :cursorCreatedAt and tournament.id < :cursorId)
                            )
                        order by tournament.createdAt desc, tournament.id desc
                        """)
        List<TournamentEntity> findPageBySportCategoryIgnoreCaseAfterCursor(String sportCategory,
                                                                                                                                                LocalDateTime cursorCreatedAt,
                                                                                                                                                UUID cursorId,
                                                                                                                                                Pageable pageable);

    @EntityGraph(attributePaths = "admin")
    List<TournamentEntity> findByAdminIdAndSportCategoryIgnoreCase(UUID adminId, String sportCategory);

    @EntityGraph(attributePaths = "admin")
    Optional<TournamentEntity> findByIdAndSportCategoryIgnoreCase(UUID id, String sportCategory);
}