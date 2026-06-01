package com.aerilon.turfclan.facility.review.controller;

import com.aerilon.turfclan.facility.review.dto.ReviewAverageResponseDto;
import com.aerilon.turfclan.facility.review.dto.ReviewCreateRequestDto;
import com.aerilon.turfclan.facility.review.dto.ReviewOwnerResponseDto;
import com.aerilon.turfclan.facility.review.dto.ReviewPublicResponseDto;
import com.aerilon.turfclan.facility.review.dto.ReviewUpdateRequestDto;
import com.aerilon.turfclan.facility.review.service.ReviewService;
import com.aerilon.turfclan.security.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/facility")
@RequiredArgsConstructor
@Tag(name = "Reviews", description = "Facility review APIs")
public class ReviewController {

    @Autowired
    private final ReviewService reviewService;

    @Autowired
    private final SecurityUtils securityUtils;

    /**
     * Creates a review for a facility by the authenticated user.
     */
    @PostMapping("/{facilityId}/reviews")
    @PreAuthorize("hasAuthority('ROLE_TM_USER')")
    @Operation(summary = "Create Review", description = "Creates a review for a facility by the authenticated user.")
    public ResponseEntity<ReviewOwnerResponseDto> createReview(
            @PathVariable UUID facilityId,
            @Valid @RequestBody ReviewCreateRequestDto request) {
        String userId = securityUtils.getCurrentUserId();
        return ResponseEntity.ok(reviewService.createReview(userId, facilityId, request));
    }

    /**
     * Updates a review created by the authenticated user.
     */
    @PutMapping("/reviews/{reviewId}")
    @PreAuthorize("hasAuthority('ROLE_TM_USER')")
    @Operation(summary = "Update Review", description = "Updates a review created by the authenticated user.")
    public ResponseEntity<ReviewOwnerResponseDto> updateReview(
            @PathVariable UUID reviewId,
            @Valid @RequestBody ReviewUpdateRequestDto request) {
        String userId = securityUtils.getCurrentUserId();
        return ResponseEntity.ok(reviewService.updateReview(userId, reviewId, request));
    }

    /**
     * Lists public reviews for a facility (rating, comment, and user name only).
     */
    @GetMapping("/{facilityId}/reviews")
    @PreAuthorize("hasAnyAuthority('ROLE_TM_USER', 'ROLE_TM_PARTNER')")
    @Operation(summary = "List Reviews", description = "Returns public reviews for a facility.")
    public ResponseEntity<List<ReviewPublicResponseDto>> getReviewsForFacility(
            @PathVariable UUID facilityId) {
        return ResponseEntity.ok(reviewService.getReviewsForFacility(facilityId));
    }

    /**
     * Marks a review as under review by the facility partner.
     */
    @PatchMapping("/reviews/{reviewId}/under-review")
    @PreAuthorize("hasAuthority('ROLE_TM_PARTNER')")
    @Operation(summary = "Flag Review", description = "Marks a review as under review by the facility partner.")
    public ResponseEntity<Void> flagReview(
            @PathVariable UUID reviewId) {
        String userId = securityUtils.getCurrentUserId();
        reviewService.markReviewUnderReview(userId, reviewId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Returns the average rating for a facility.
     */
    @GetMapping("/{facilityId}/reviews/average")
    @PreAuthorize("hasAnyAuthority('ROLE_TM_USER', 'ROLE_TM_PARTNER')")
    @Operation(summary = "Get Average Rating", description = "Returns the average rating for a facility.")
    public ResponseEntity<ReviewAverageResponseDto> getAverageRating(
            @PathVariable UUID facilityId) {
        return ResponseEntity.ok(reviewService.getAverageRating(facilityId));
    }
}
