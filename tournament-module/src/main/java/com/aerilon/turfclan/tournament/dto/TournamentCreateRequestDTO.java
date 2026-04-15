package com.aerilon.turfclan.tournament.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class TournamentCreateRequestDTO {

    @NotBlank(message = "Tournament name is required")
    private String name;

    @NotBlank(message = "Location name is required")
    private String locationName;

    @NotBlank(message = "Geolocation is required")
    private String geolocation;

    private BigDecimal entryFee;

    private BigDecimal prizePool;

    @NotBlank(message = "About is required")
    private String about;

    @NotBlank(message = "Format is required")
    private String format;

    @NotNull(message = "Register by is required")
    private LocalDate registerBy;

    @NotBlank(message = "Why join is required")
    private String whyJoin;

    @NotBlank(message = "Rules are required")
    private String rules;
}