package com.aerilon.turfclan.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ManualBookingRequestDTO {

    @NotNull(message = "Sub-facility ID is mandatory")
    private UUID subFacilityId;

    @NotNull(message = "Booking date is mandatory")
    @FutureOrPresent(message = "Cannot book for a past date")
    private LocalDate bookingDate;

    @NotNull(message = "Start time is mandatory")
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime startTime;

    @NotNull(message = "End time is mandatory")
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime endTime;

    @NotNull(message = "Player count is required")
    @Min(value = 1, message = "Player count must be at least 1")
    private Integer playerCount;

    private String userNote;
}
