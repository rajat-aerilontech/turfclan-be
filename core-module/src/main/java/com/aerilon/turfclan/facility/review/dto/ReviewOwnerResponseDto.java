package com.aerilon.turfclan.facility.review.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class ReviewOwnerResponseDto {

    private UUID id;
    private BigDecimal rating;
    private String preReview;
    private String comment;
    private Boolean underReview;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
