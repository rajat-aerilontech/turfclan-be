package com.aerilon.turfclan.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookingRequestDTO {

    @NotNull(message = "Sport ID is mandatory")
    private UUID sportId;

    @NotNull(message = "Booking date is mandatory")
    @FutureOrPresent(message = "Cannot book for a past date")
    private LocalDate bookingDate;

    @NotNull(message = "Start time is mandatory")
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime startTime;

    @NotNull(message = "End time is mandatory")
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime endTime;

    @NotNull(message = "Total amount is required")
    private Double totalAmount;

    /**
     * If true: Status becomes PENDING_APPROVAL (Partner must accept)
     * If false: Status becomes PENDING_PAYMENT (User must pay online)
     */
    private boolean payAtVenue;

    @NotNull(message = "Player count is required")
    @Min(value = 1, message = "Player count must be at least 1")
    private Integer playerCount;

    private String userNote;
}
