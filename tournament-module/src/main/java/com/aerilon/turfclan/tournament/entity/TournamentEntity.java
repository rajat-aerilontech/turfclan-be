package com.aerilon.turfclan.tournament.entity;

import com.aerilon.turfclan.entity.BaseAuditableEntity;
import com.aerilon.turfclan.facility.entity.FacilityEntity;
import com.aerilon.turfclan.sportsdirectory.entity.SportOrganizationEntity;
import com.aerilon.turfclan.tournament.enums.TournamentStatus;
import com.aerilon.turfclan.user.entity.UserEntity;
import jakarta.persistence.*;
import org.locationtech.jts.geom.Point;
import lombok.Getter;
import lombok.Setter;


import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "t_tournaments", schema = "turfclan_schema")
@Getter
@Setter
public class TournamentEntity extends BaseAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "facility_id", referencedColumnName = "id")
    private FacilityEntity facility;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", referencedColumnName = "id")
    private SportOrganizationEntity organization;

    @Column(name = "sport_category", nullable = false, length = 100)
    private String sportCategory;

    @Column(name = "is_public_property")
    private boolean isPublicProperty;

    @Column(name = "tournament_name", nullable = false, length = 255)
    private String tournamentName;

    @Column(name = "location_name")
    private String locationName;

    @Column(name = "custom_location_address")
    private String customLocationAddress;

    @Column(name = "geo_location", columnDefinition = "geometry(Point, 4326)", nullable = false)
    private Point geoLocation;

    @Column(name = "entry_fee", precision = 12, scale = 2)
    private BigDecimal entryFee;

    @Column(name = "prize_pool", precision = 12, scale = 2)
    private BigDecimal prizePool;

    @Column(name = "about", nullable = false, columnDefinition = "text")
    private String about;

    @Column(name = "format", nullable = false, columnDefinition = "text")
    private String format;

    @Column(name = "rules", columnDefinition = "text")
    private String rules;

    @Column(name = "why_join", columnDefinition = "text")
    private String whyJoin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id", referencedColumnName = "id", nullable = false)
    private UserEntity admin;

    @Enumerated(EnumType.STRING)
    @Column(name = "tournament_status", nullable = false)
    private TournamentStatus tournamentStatus;
}