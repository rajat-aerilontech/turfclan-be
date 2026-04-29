package com.aerilon.turfclan.controller;

import com.aerilon.turfclan.dto.BookingRequestDTO;
import com.aerilon.turfclan.dto.BookingResponseDTO;
import com.aerilon.turfclan.dto.SlotResponseDTO;
import com.aerilon.turfclan.enums.BookingStatus;
import com.aerilon.turfclan.service.BookingService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/booking")
@RequiredArgsConstructor
@Tag(name = "Booking", description = "Booking APIs")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    // 1. MOBILE USER: Get the availability grid for a specific sport/date
    @GetMapping("/availability/{sportId}")
    @PreAuthorize("hasAuthority('ROLE_TM_USER')")
    public ResponseEntity<List<SlotResponseDTO>> getAvailability(
            @PathVariable UUID sportId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(bookingService.getAvailableSlots(sportId, date));
    }

    // 2. MOBILE USER: Create the booking (Cash or Online)
    @PostMapping("/reserve")
    @PreAuthorize("hasAuthority('ROLE_TM_USER')")
    public ResponseEntity<BookingResponseDTO> reserveSlot(
            Authentication authentication,
            @Valid @RequestBody BookingRequestDTO request) {
        String userId = authentication.getName();
        return ResponseEntity.ok(bookingService.createBooking(request, userId));
    }

    // 3. PARTNER: Show all "Pay in Cash" bookings that need approval
    @GetMapping("/partner/requests/{facilityId}")
    @PreAuthorize("hasAuthority('ROLE_TM_PARTNER')")
    public ResponseEntity<List<BookingResponseDTO>> getRequestsForPartner(@PathVariable UUID facilityId) {
        return ResponseEntity.ok(bookingService.getPendingPartnerRequests(facilityId));
    }

    // 4. PARTNER: Accept or Reject a specific booking
    @PatchMapping("/{bookingId}/status")
    @PreAuthorize("hasAuthority('ROLE_TM_PARTNER')")
    public ResponseEntity<Void> updateStatus(
            @PathVariable UUID bookingId,
            @RequestParam BookingStatus status) {
        bookingService.handlePartnerAction(bookingId, status);
        return ResponseEntity.noContent().build();
    }

}
