package com.aerilon.turfclan.facility.review.service.impl;

import com.aerilon.turfclan.exception.InvalidRequestException;
import com.aerilon.turfclan.exception.ResourceNotFoundException;
import com.aerilon.turfclan.exception.UnauthorizedAccessException;
import com.aerilon.turfclan.facility.entity.FacilityEntity;
import com.aerilon.turfclan.facility.review.dto.ReviewAverageResponseDto;
import com.aerilon.turfclan.facility.review.dto.ReviewCreateRequestDto;
import com.aerilon.turfclan.facility.review.dto.ReviewOwnerResponseDto;
import com.aerilon.turfclan.facility.review.dto.ReviewPublicResponseDto;
import com.aerilon.turfclan.facility.review.dto.ReviewUpdateRequestDto;
import com.aerilon.turfclan.facility.review.entity.ReviewEntity;
import com.aerilon.turfclan.facility.review.repository.ReviewAverageProjection;
import com.aerilon.turfclan.facility.review.repository.ReviewRepository;
import com.aerilon.turfclan.facility.review.service.ReviewService;
import com.aerilon.turfclan.partner.repository.FacilityRepository;
import com.aerilon.turfclan.user.entity.UserEntity;
import com.aerilon.turfclan.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final FacilityRepository facilityRepository;

    @Override
    @Transactional
    public ReviewOwnerResponseDto createReview(String userId, UUID facilityId, ReviewCreateRequestDto request) {
        UUID userUuid = parseUserId(userId);
        UserEntity user = userRepository.findById(userUuid)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (reviewRepository.existsByUserIdAndFacilityId(userUuid, facilityId)) {
            throw new InvalidRequestException("Review already exists for this facility");
        }

        FacilityEntity facility = facilityRepository.findById(facilityId)
                .orElseThrow(() -> new ResourceNotFoundException("Facility not found"));

        ReviewEntity review = new ReviewEntity();
        review.setUser(user);
        review.setFacility(facility);
        review.setRating(validateRating(request.getRating()));
        review.setPreReview(cleanOptional(request.getPreReview()));
        review.setComment(cleanOptional(request.getComment()));
        review.setUnderReview(false);
        review.setCreatedAt(LocalDateTime.now());

        ReviewEntity saved = reviewRepository.save(review);
        return toOwnerDto(saved);
    }

    @Override
    @Transactional
    public ReviewOwnerResponseDto updateReview(String userId, UUID reviewId, ReviewUpdateRequestDto request) {
        UUID userUuid = parseUserId(userId);
        ReviewEntity review = reviewRepository.findWithDetailsById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));

        if (review.getUser() == null || !review.getUser().getId().equals(userUuid)) {
            throw new UnauthorizedAccessException("Unauthorized: Review does not belong to this user");
        }

        if (request.getRating() != null) {
            review.setRating(validateRating(request.getRating()));
        }
        if (request.getPreReview() != null) {
            review.setPreReview(cleanOptional(request.getPreReview()));
        }
        if (request.getComment() != null) {
            review.setComment(cleanOptional(request.getComment()));
        }
        review.setUpdatedAt(LocalDateTime.now());

        ReviewEntity saved = reviewRepository.save(review);
        return toOwnerDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewPublicResponseDto> getReviewsForFacility(UUID facilityId) {
        return reviewRepository.findByFacilityIdAndUnderReviewFalseOrderByCreatedAtDesc(facilityId)
                .stream()
                .map(this::toPublicDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void markReviewUnderReview(String partnerUserId, UUID reviewId) {
        UUID partnerUuid = parseUserId(partnerUserId);
        ReviewEntity review = reviewRepository.findWithDetailsById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));

        if (review.getFacility() == null
            || review.getFacility().getUser() == null
            || !review.getFacility().getUser().getId().equals(partnerUuid)) {
            throw new UnauthorizedAccessException("Unauthorized: Facility does not belong to this partner");
        }

        if (Boolean.TRUE.equals(review.getUnderReview())) {
            return;
        }

        review.setUnderReview(true);
        review.setUpdatedAt(LocalDateTime.now());
        reviewRepository.save(review);
    }

    @Override
    @Transactional(readOnly = true)
    public ReviewAverageResponseDto getAverageRating(UUID facilityId) {
        ReviewAverageProjection projection = reviewRepository.getAverageRating(facilityId);
        ReviewAverageResponseDto dto = new ReviewAverageResponseDto();
        dto.setAverageRating(projection != null && projection.getAverageRating() != null
                ? round(projection.getAverageRating())
                : 0.0);
        dto.setTotalReviews(projection != null && projection.getTotalReviews() != null
                ? projection.getTotalReviews()
                : 0L);
        return dto;
    }

    private UUID parseUserId(String userId) {
        try {
            return UUID.fromString(userId);
        } catch (Exception ex) {
            throw new UnauthorizedAccessException("Unauthorized: invalid user");
        }
    }

    private BigDecimal validateRating(BigDecimal rating) {
        if (rating == null) {
            throw new InvalidRequestException("Rating is required");
        }
        if (rating.compareTo(BigDecimal.ONE) < 0 || rating.compareTo(BigDecimal.valueOf(5)) > 0) {
            throw new InvalidRequestException("Rating must be between 1 and 5");
        }
        BigDecimal scaled = rating.multiply(BigDecimal.valueOf(2));
        if (scaled.remainder(BigDecimal.ONE).compareTo(BigDecimal.ZERO) != 0) {
            throw new InvalidRequestException("Rating must be in 0.5 increments");
        }
        return rating.setScale(1, RoundingMode.HALF_UP);
    }

    private String cleanOptional(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private ReviewOwnerResponseDto toOwnerDto(ReviewEntity review) {
        ReviewOwnerResponseDto dto = new ReviewOwnerResponseDto();
        dto.setId(review.getId());
        dto.setRating(review.getRating());
        dto.setPreReview(review.getPreReview());
        dto.setComment(review.getComment());
        dto.setUnderReview(review.getUnderReview());
        dto.setCreatedAt(review.getCreatedAt());
        dto.setUpdatedAt(review.getUpdatedAt());
        return dto;
    }

    private ReviewPublicResponseDto toPublicDto(ReviewEntity review) {
        ReviewPublicResponseDto dto = new ReviewPublicResponseDto();
        dto.setRating(review.getRating());
        dto.setComment(review.getComment());
        dto.setUserName(buildDisplayName(review.getUser()));
        return dto;
    }

    private String buildDisplayName(UserEntity user) {
        if (user == null) {
            return "Unknown";
        }
        String firstName = cleanOptional(user.getFirstName());
        String lastName = cleanOptional(user.getLastName());
        if (firstName != null && lastName != null) {
            return firstName + " " + lastName;
        }
        if (firstName != null) {
            return firstName;
        }
        if (lastName != null) {
            return lastName;
        }
        if (user.getUserName() != null && !user.getUserName().isBlank()) {
            return user.getUserName();
        }
        return "User";
    }

    private double round(double value) {
        return Math.round(value * 10.0) / 10.0;
    }
}
