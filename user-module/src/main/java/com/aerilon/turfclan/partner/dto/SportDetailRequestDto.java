package com.aerilon.turfclan.partner.dto;

import com.aerilon.turfclan.enums.Sports;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.Set;

@Getter
@Setter
public class SportDetailRequestDto {
    @NotNull(message = "Sport type is required")
    private Sports sportType;
    @NotBlank(message = "Sub-type is required")
    private String subType;
    @NotNull(message = "Number of units is required")
    @Min(value = 1, message = "Number of units must be at least 1")
    private Integer numberOfUnits;
    @NotNull(message = "Max players per unit is required")
    @Min(value = 1, message = "At least 1 player per unit is required")
    private Integer maxPlayersPerUnit;
    private String currency;
    @NotNull(message = "Price per hour is required")
    @Positive(message = "Price per hour must be greater than zero")
    private BigDecimal pricePerHour;
    @Positive(message = "Price per session must be positive")
    private BigDecimal pricePerSession;
    @NotNull(message = "Opening time is required")
    private LocalTime openTime;
    @NotNull(message = "Closing time is required")
    private LocalTime closeTime;
    @NotEmpty(message = "At least one available day must be selected")
    private Set<String> availableDays;
    @NotNull(message = "Length is required")
    @Positive(message = "Length must be greater than zero")
    private Double length;
    @NotNull(message = "Width is required")
    @Positive(message = "Width must be greater than zero")
    private Double width;
    @NotBlank(message = "Surface type is required (e.g., Natural Grass, Turf)")
    private String surfaceType;
    private Set<String> amenities;
}
