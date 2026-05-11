package com.aerilon.turfclan.controller;

import com.aerilon.turfclan.dto.BookingRequestDTO;
import com.aerilon.turfclan.dto.BookingResponseDTO;
import com.aerilon.turfclan.dto.SlotResponseDTO;
import com.aerilon.turfclan.enums.BookingStatus;
import com.aerilon.turfclan.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
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
    /**
     * Returns available booking slots for a sport on a given date.
     *
     * @param sportId sport identifier
     * @param date date to check availability
     * @return list of available slots
     */
    @GetMapping("/availability/{sportId}")
    @PreAuthorize("hasAuthority('ROLE_TM_USER')")
    @Operation(
        summary = "Get Availability Slots",
        description = "Returns available time slots foa a sport on a specific date. Requires turf-mobile source-app."
    )
    public ResponseEntity<List<SlotResponseDTO>> getAvailability(
            @PathVariable UUID sportId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(bookingService.getAvailableSlots(sportId, date));
    }

    // 2. MOBILE USER: Create the booking (Cash or Online)
    /**
     * Creates a booking for the authenticated user.
     *
     * @param authentication authenticated principal containing the user id
     * @param request booking request payload
     * @return created booking
     */
    @PostMapping("/reserve")
    @PreAuthorize("hasAuthority('ROLE_TM_USER')")
    @Operation(
            summary = "Reserve Slot",
            description = "Creates a booking for the user(cash or online)."
    )
    public ResponseEntity<BookingResponseDTO> reserveSlot(
            Authentication authentication,
            @Valid @RequestBody BookingRequestDTO request) {
        String userId = authentication.getName();
        return ResponseEntity.ok(bookingService.createBooking(request, userId));
    }

    // 3. PARTNER: Show all "Pay in Cash" bookings that need approval
    /**
     * Returns pending pay-in-cash booking requests for a facility.
     *
     * @param facilityId facility identifier
     * @return list of pending requests
     */
    @GetMapping("/partner/requests/{facilityId}")
    @PreAuthorize("hasAuthority('ROLE_TM_PARTNER')")
    @Operation(
            summary = "Get Pending Partner Request",
            description = "Returns pending pay-in-cash booking requests for a facility"
    )
    public ResponseEntity<List<BookingResponseDTO>> getRequestsForPartner(@PathVariable UUID facilityId) {
        return ResponseEntity.ok(bookingService.getPendingPartnerRequests(facilityId));
    }

    // 4. PARTNER: Accept or Reject a specific booking
    /**
     * Updates the status for a booking.
     *
     * @param bookingId booking identifier
     * @param status new booking status
     * @return empty response on success
     */
    @PatchMapping("/{bookingId}/status")
    @PreAuthorize("hasAuthority('ROLE_TM_PARTNER')")
    @Operation(
            summary = "Update Booking Status",
            description = "Accepts or rejects a booking by updating its status."
    )
    public ResponseEntity<Void> updateStatus(
            @PathVariable UUID bookingId,
            @RequestParam BookingStatus status) {
        bookingService.handlePartnerAction(bookingId, status);
        return ResponseEntity.noContent().build();
    }

}
