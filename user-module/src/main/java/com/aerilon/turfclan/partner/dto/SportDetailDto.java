package com.aerilon.turfclan.partner.dto;

import com.aerilon.turfclan.enums.Sports;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.Set;

@Getter
@Setter
public class SportDetailDto {
    private Sports sportType;
    private String subType;
    private Integer numberOfUnits;
    private Integer maxPlayersPerUnit;
    private String currency;
    private BigDecimal pricePerHour;
    private BigDecimal pricePerSession;
    private LocalTime openTime;
    private LocalTime closeTime;
    private Set<String> availableDays;
    private Double length;
    private Double width;
    private String surfaceType;
    private Set<String> amenities;
}
