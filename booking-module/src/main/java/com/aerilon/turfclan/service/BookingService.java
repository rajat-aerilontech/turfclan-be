package com.aerilon.turfclan.service;

import com.aerilon.turfclan.dto.BookingRequestDTO;
import com.aerilon.turfclan.dto.BookingResponseDTO;
import com.aerilon.turfclan.dto.SlotResponseDTO;
import com.aerilon.turfclan.enums.BookingStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface BookingService {
    List<SlotResponseDTO> getAvailableSlots(UUID sportId, LocalDate date);
    BookingResponseDTO createBooking(BookingRequestDTO request, String userId);
    List<BookingResponseDTO> getPendingPartnerRequests(UUID facilityId);
    List<BookingResponseDTO> getPartnerBookings(String userId);
    void handlePartnerAction(UUID bookingId, BookingStatus status);
}
