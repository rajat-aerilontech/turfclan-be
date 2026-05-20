package com.aerilon.turfclan.entity;

import com.aerilon.turfclan.enums.BookingStatus;
import com.aerilon.turfclan.facility.entity.SubFacilityEntity;
import com.aerilon.turfclan.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "t_booking", schema = "turfclan_schema")
@Getter
@Setter
public class BookingEntity extends BaseAuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sub_facility_id", nullable = false)
    private SubFacilityEntity sport;

    @Column(name = "booking_date", nullable = false)
    private LocalDate bookingDate;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "booking_status", nullable = false)
    private BookingStatus bookingStatus;

    @Column(name = "final_price", nullable = false)
    private Double finalPrice;

    @Column(name = "is_prime_time")
    private Boolean isPrimeTime;

    @Column(name = "total_amount", nullable = false)
    private Double totalAmount;

    @Column(name = "player_count", nullable = false)
    private Integer playerCount;

    @Version
    private Integer version;

    @Column(name = "payment_id")
    private String paymentId;

    @Column(name = "user_note")
    private String userNote;
}
