package com.aerilon.turfclan.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SlotResponseDTO {
    private LocalTime startTime;
    private LocalTime endTime;
    private boolean isAvailable;
    private BigDecimal price;
    private String currency;
}