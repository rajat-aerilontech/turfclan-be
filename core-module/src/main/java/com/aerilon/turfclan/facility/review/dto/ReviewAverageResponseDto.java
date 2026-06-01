package com.aerilon.turfclan.facility.review.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewAverageResponseDto {

    private Double averageRating;
    private Long totalReviews;
}
