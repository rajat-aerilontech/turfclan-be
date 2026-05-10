package com.aerilon.turfclan.dto;

import com.aerilon.turfclan.enums.BookingStatus;
import com.aerilon.turfclan.enums.Sports;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponseDTO {
    private UUID bookingId;

    // Brand & Facility info for the user
    private String facilityName;

    // Sport specific info
    private Sports sportType;
    private String subType;

    // Time and Date
    private LocalDate bookingDate;
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime startTime;
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime endTime;

    // Pricing and Status
    private Double totalAmount;
    private BookingStatus status;
    private String paymentId; // Will be null for "Pay at Venue" until confirmed

    // Partner Dashboard specific fields
    private String userName;
    private String userNote;
}
