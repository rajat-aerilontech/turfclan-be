package com.aerilon.turfclan.service.impl;

import com.aerilon.turfclan.converter.BookingConverter;
import com.aerilon.turfclan.dto.BookingRequestDTO;
import com.aerilon.turfclan.dto.BookingResponseDTO;
import com.aerilon.turfclan.dto.SlotResponseDTO;
import com.aerilon.turfclan.entity.BookingEntity;
import com.aerilon.turfclan.enums.BookingStatus;
import com.aerilon.turfclan.exception.BookingConflictException;
import com.aerilon.turfclan.exception.InvalidRequestException;
import com.aerilon.turfclan.exception.ResourceNotFoundException;
import com.aerilon.turfclan.facility.entity.SubFacilityEntity;
import com.aerilon.turfclan.partner.repository.SportDetailRepository;
import com.aerilon.turfclan.repository.BookingRepository;
import com.aerilon.turfclan.service.BookingService;
import com.aerilon.turfclan.user.entity.UserEntity;
import com.aerilon.turfclan.user.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final UserRepository userRepository;
    private final SportDetailRepository sportDetailRepository;
    private final BookingRepository bookingRepository;
    private final BookingConverter bookingConverter;

    /**
     * Logic: Generates a time grid. A slot is 'Available' only if the number
     * of active bookings is less than the total 'numberOfUnits' for that sport.
     */
    @Transactional(readOnly = true)
    public List<SlotResponseDTO> getAvailableSlots(UUID sportId, LocalDate date) {
        SubFacilityEntity sport = sportDetailRepository.findById(sportId)
                .orElseThrow(() -> new RuntimeException("Sport configuration not found"));
        List<SlotResponseDTO> grid = new ArrayList<>();
        LocalTime current = sport.getOpenTime();
        while (current.isBefore(sport.getCloseTime())) {
            LocalTime slotEnd = current.plusMinutes(sport.getSlotDurationMinutes());
            long occupiedUnits = getOccupiedUnitCount(sportId, date, current, slotEnd);
            boolean isAvailable = occupiedUnits < sport.getNumberOfUnits();
            BigDecimal finalPrice = calculateDynamicPrice(sport, current);
            grid.add(new SlotResponseDTO(current, slotEnd, isAvailable, finalPrice, sport.getCurrency()));
            int buffer = sport.getBufferDuration() != null ? sport.getBufferDuration() : 0;
            current = slotEnd.plusMinutes(buffer);
        }
        return grid;
    }

    /**
     * Logic: Creates the booking. Uses Optimistic Locking (@Version) in the entity
     * to prevent two users from grabbing the last unit at the same time.
     */
    @Transactional
    public BookingResponseDTO createBooking(BookingRequestDTO request, String userId) {
        SubFacilityEntity sport = sportDetailRepository.findById(request.getSportId())
                .orElseThrow(() -> new ResourceNotFoundException("Sport details not found"));
        UserEntity user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        validateBookingTime(sport, request.getStartTime(), request.getEndTime());
        long occupied = getOccupiedUnitCount(request.getSportId(), request.getBookingDate(),
                request.getStartTime(), request.getEndTime());
        if (occupied >= sport.getNumberOfUnits()) {
            throw new BookingConflictException("This slot is now fully booked. Please select another time.");
        }
        BookingEntity booking = bookingConverter.toEntity(request, sport, user);
        BigDecimal backendPrice = calculateDynamicPrice(sport, request.getStartTime());
        booking.setTotalAmount(backendPrice.doubleValue());
        booking.setFinalPrice(backendPrice.doubleValue());
        booking.setBookingStatus(request.isPayAtVenue() ?
                BookingStatus.PENDING_APPROVAL : BookingStatus.PENDING_PAYMENT);
        booking.setCreatedAt(LocalDateTime.now());
        return bookingConverter.toDto(bookingRepository.save(booking));
    }

    /**
     * Logic: Partner-facing endpoint to see unpaid requests for a specific facility.
     */
    @Transactional(readOnly = true)
    public List<BookingResponseDTO> getPendingPartnerRequests(UUID facilityId) {
        return bookingRepository.findPendingApprovalByFacility(facilityId, BookingStatus.PENDING_APPROVAL)
                .stream()
                .map(bookingConverter::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Returns all bookings for facilities owned by the authenticated partner.
     */
    @Transactional(readOnly = true)
    public List<BookingResponseDTO> getPartnerBookings(String userId) {
        UUID partnerUserId = UUID.fromString(userId);
        return bookingRepository.findByPartnerUserId(partnerUserId)
                .stream()
                .map(bookingConverter::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Logic: Updates status based on Partner Action (Accept/Reject).
     */
    @Transactional
    public void handlePartnerAction(UUID bookingId, BookingStatus status) {
        BookingEntity booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        if (status == BookingStatus.CONFIRMED || status == BookingStatus.REJECTED) {
            booking.setBookingStatus(status);
            booking.setUpdatedAt(LocalDateTime.now());
            bookingRepository.save(booking);
        } else {
            throw new IllegalArgumentException("Invalid status update for partner.");
        }
    }

    private BigDecimal calculateDynamicPrice(SubFacilityEntity sport, LocalTime time) {
        BigDecimal price = sport.getPricePerHour();
        if (time.isAfter(LocalTime.of(17, 59))) {
            return price.multiply(BigDecimal.valueOf(1.2));
        }
        return price;
    }

    /**
     * check how many units are occupied for a specific time slot.
     * We consider CONFIRMED, PENDING_PAYMENT, and PENDING_APPROVAL as "Occupying" a unit.
     */
    private long getOccupiedUnitCount(UUID sportId, LocalDate date, LocalTime start, LocalTime end) {
        List<BookingStatus> ACTIVE_STATUSES = List.of(
                BookingStatus.CONFIRMED,
                BookingStatus.PENDING_PAYMENT,
                BookingStatus.PENDING_APPROVAL
        );
        return bookingRepository.countOverlappingBookings(sportId, date, start, end, ACTIVE_STATUSES);
    }

    /**
     * Validates that the requested time is within operating hours and matches the required slot duration.
     */
    private void validateBookingTime(SubFacilityEntity sport, LocalTime start, LocalTime end) {
        // Check Operating Hours
        if (start.isBefore(sport.getOpenTime()) || end.isAfter(sport.getCloseTime())) {
            throw new InvalidRequestException(String.format(
                    "Time falls outside operating hours (%s - %s)", sport.getOpenTime(), sport.getCloseTime()));
        }
        // Check Slot Duration
        long requestedDuration = java.time.Duration.between(start, end).toMinutes();
        if (requestedDuration != sport.getSlotDurationMinutes()) {
            throw new InvalidRequestException("Invalid duration. Required: " + sport.getSlotDurationMinutes() + " mins");
        }
    }
}
