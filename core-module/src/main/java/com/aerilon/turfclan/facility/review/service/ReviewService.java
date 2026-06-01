package com.aerilon.turfclan.facility.review.service;

import com.aerilon.turfclan.facility.review.dto.ReviewAverageResponseDto;
import com.aerilon.turfclan.facility.review.dto.ReviewCreateRequestDto;
import com.aerilon.turfclan.facility.review.dto.ReviewOwnerResponseDto;
import com.aerilon.turfclan.facility.review.dto.ReviewPublicResponseDto;
import com.aerilon.turfclan.facility.review.dto.ReviewUpdateRequestDto;

import java.util.List;
import java.util.UUID;

public interface ReviewService {

    ReviewOwnerResponseDto createReview(String userId, UUID facilityId, ReviewCreateRequestDto request);

    ReviewOwnerResponseDto updateReview(String userId, UUID reviewId, ReviewUpdateRequestDto request);

    List<ReviewPublicResponseDto> getReviewsForFacility(UUID facilityId);

    void markReviewUnderReview(String partnerUserId, UUID reviewId);

    ReviewAverageResponseDto getAverageRating(UUID facilityId);
}
