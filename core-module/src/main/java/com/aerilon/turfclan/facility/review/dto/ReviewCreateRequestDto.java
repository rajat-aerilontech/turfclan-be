package com.aerilon.turfclan.facility.review.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ReviewCreateRequestDto {

    @NotNull(message = "Rating is required")
    private BigDecimal rating;

    @Size(max = 500, message = "Pre-review text must be at most 500 characters")
    private String preReview;

    @Size(max = 500, message = "Comment must be at most 500 characters")
    private String comment;
}
