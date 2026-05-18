package com.aerilon.turfclan.partner.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubFacilityDto {
    private String id;
    private String sportType;
    private String subType;
    private Integer numberOfUnits;
    private Integer maxPlayersPerUnit;
    private String currency;
    private BigDecimal pricePerHour;
    private BigDecimal pricePerSession;
    private String openTime;
    private String closeTime;
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