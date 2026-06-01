package com.aerilon.turfclan.facility.review.repository;

import com.aerilon.turfclan.facility.review.entity.ReviewEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReviewRepository extends JpaRepository<ReviewEntity, UUID> {

    boolean existsByUserIdAndFacilityId(UUID userId, UUID facilityId);

    @EntityGraph(attributePaths = {"user"})
    List<ReviewEntity> findByFacilityIdAndUnderReviewFalseOrderByCreatedAtDesc(UUID facilityId);

    @EntityGraph(attributePaths = {"user", "facility", "facility.user"})
    Optional<ReviewEntity> findWithDetailsById(UUID id);

    @Query("SELECT AVG(r.rating) as averageRating, COUNT(r) as totalReviews " +
            "FROM ReviewEntity r WHERE r.facility.id = :facilityId AND r.underReview = false")
        ReviewAverageProjection getAverageRating(@Param("facilityId") UUID facilityId);
}
