package com.aerilon.turfclan.facility.review.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ReviewPublicResponseDto {

    private BigDecimal rating;
    private String comment;
    private String userName;
}
