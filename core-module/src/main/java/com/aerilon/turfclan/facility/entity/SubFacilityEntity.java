package com.aerilon.turfclan.facility.entity;

import com.aerilon.turfclan.entity.BaseAuditableEntity;
import com.aerilon.turfclan.enums.Sports;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.Set;
import java.util.UUID;
import java.util.HashSet;

@Entity
@Table(name = "t_sub_facility", schema = "turfclan_schema")
@Getter
@Setter
public class SubFacilityEntity extends BaseAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "facility_id", nullable = false)
    private FacilityEntity facility;

    @Enumerated(EnumType.STRING)
    @Column(name = "sport_type", nullable = false)
    private Sports sportType;

    @Column(name = "sub_type", nullable = false)
    private String subType;

    @Column(name = "number_of_units", nullable = false)
    private Integer numberOfUnits;

    @Column(name = "max_players_per_unit", nullable = false)
    private Integer maxPlayersPerUnit;

    @Column(name = "currency", nullable = false)
    private String currency;

    @Column(name = "price_per_hour", nullable = false)
    private BigDecimal pricePerHour;

    @Column(name = "price_per_session")
    private BigDecimal pricePerSession;

    @Column(name = "open_time", nullable = false)
    private LocalTime openTime;

    @Column(name = "close_time", nullable = false)
    private LocalTime closeTime;

    @Column(name = "slot_duration_minutes", nullable = false)
    private Integer slotDurationMinutes;

    @Column(name = "buffer_duration")
    private Integer bufferDuration;

    @Column(name = "prime_time_surge_percentage")
    private BigDecimal primeTimeSurgePercentage;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "prime_time_windows", columnDefinition = "text[]")
    private Set<String> primeTimeWindows; // e.g., {"18:00-22:00"}

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "available_days", columnDefinition = "text[]", nullable = false)
    private Set<String> availableDays = new HashSet<>();

    @Column(name = "length_m", nullable = false)
    private Double length;

    @Column(name = "width_m", nullable = false)
    private Double width;

    @Column(name = "surface_type", nullable = false)
    private String surfaceType;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "amenities", columnDefinition = "text[]")
    private Set<String> amenities = new HashSet<>();
}