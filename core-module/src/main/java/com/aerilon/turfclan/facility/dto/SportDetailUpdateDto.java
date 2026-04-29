package com.aerilon.turfclan.facility.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SportDetailUpdateDto {

    private UUID sportDetailId;
    private String subType;
    private Integer numberOfUnits;
    private Integer maxPlayersPerUnit;
    private String currency;
    private BigDecimal pricePerHour;
    private BigDecimal pricePerSession;
    private LocalTime openTime;
    private LocalTime closeTime;
    private Integer slotDurationMinutes;
    private Integer bufferDuration;
    private BigDecimal primeTimeSurgePercentage;
    private Set<String> primeTimeWindows;
    private Set<String> availableDays;
    private Double length;
    private Double width;
    private String surfaceType;
    private Set<String> amenities;
}

