package com.aerilon.turfclan.repository;

import com.aerilon.turfclan.entity.BookingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.aerilon.turfclan.enums.BookingStatus;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface BookingRepository extends JpaRepository<BookingEntity, UUID> {

    @Query("SELECT b FROM BookingEntity b WHERE b.sport.id = :sportId AND b.bookingDate = :date " +
            "AND b.bookingStatus IN :activeStatuses")
    List<BookingEntity> findActiveBookingsByDate(
            @Param("sportId") UUID sportId,
            @Param("date") LocalDate date,
            @Param("activeStatuses") List<BookingStatus> activeStatuses);

    @Query("SELECT COUNT(b) FROM BookingEntity b WHERE b.sport.id = :sportId AND b.bookingDate = :date " +
            "AND b.startTime < :end AND b.endTime > :start " +
            "AND b.bookingStatus IN :activeStatuses")
    long countOverlappingBookings(
            @Param("sportId") UUID sportId,
            @Param("date") LocalDate date,
            @Param("start") LocalTime start,
            @Param("end") LocalTime end,
            @Param("activeStatuses") List<BookingStatus> activeStatuses); // Renamed for clarity

    @Query("SELECT b FROM BookingEntity b WHERE b.sport.facility.id = :facilityId " +
            "AND b.bookingStatus = :status")
    List<BookingEntity> findPendingApprovalByFacility(
            @Param("facilityId") UUID facilityId,
            @Param("status") BookingStatus status);
}
