package com.aerilon.turfclan.tournament.entity;

import com.aerilon.turfclan.entity.BaseAuditableEntity;
import com.aerilon.turfclan.enums.RecordStatus;
import com.aerilon.turfclan.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "t_tournaments", schema = "turfclan_schema")
@Getter
@Setter
public class TournamentEntity extends BaseAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "sport_category", nullable = false, length = 100)
    private String sportCategory;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "location_name", nullable = false, length = 255)
    private String locationName;

    @Column(name = "geolocation", nullable = false, length = 500)
    private String geolocation;

    @Column(name = "entry_fee", precision = 12, scale = 2)
    private BigDecimal entryFee;

    @Column(name = "prize_pool", precision = 12, scale = 2)
    private BigDecimal prizePool;

    @Column(name = "about", nullable = false, columnDefinition = "text")
    private String about;

    @Column(name = "format", nullable = false, columnDefinition = "text")
    private String format;

    @Column(name = "register_by", nullable = false)
    private LocalDate registerBy;

    @Column(name = "why_join", nullable = false, columnDefinition = "text")
    private String whyJoin;

    @Column(name = "rules", nullable = false, columnDefinition = "text")
    private String rules;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id", referencedColumnName = "id", nullable = false)
    private UserEntity admin;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 255, nullable = false)
    private RecordStatus status;
}