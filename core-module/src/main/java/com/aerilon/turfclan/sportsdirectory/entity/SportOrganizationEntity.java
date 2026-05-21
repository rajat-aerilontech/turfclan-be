package com.aerilon.turfclan.sportsdirectory.entity;

import com.aerilon.turfclan.entity.BaseAuditableEntity;
import com.aerilon.turfclan.enums.RecordStatus;
import com.aerilon.turfclan.enums.Sports;
import com.aerilon.turfclan.sportsdirectory.enums.OrganizationType;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.locationtech.jts.geom.Point;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "t_sport_organizations", schema = "turfclan_schema", uniqueConstraints = {
        @UniqueConstraint(columnNames = "name")})
@Getter
@Setter
public class SportOrganizationEntity extends BaseAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "organization_type", length = 50, nullable = false)
    private OrganizationType organizationType;

    @Enumerated(EnumType.STRING)
    @Column(name = "sport_category", length = 100, nullable = false)
    private Sports sportCategory;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "images", columnDefinition = "jsonb")
    private List<String> images;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "short_name", length = 100)
    private String shortName;

    @Column(name = "board", length = 255)
    private String board;

    @Column(name = "members_number")
    private Integer membersNumber;

    @Column(name = "founded_year")
    private Integer foundedYear;

    @Column(name = "state", length = 100)
    private String state;

    @Column(name = "location_name", length = 255)
    private String locationName;

    @Column(name = "map_location", columnDefinition = "text")
    private String mapLocation;

    /**
     * SRID 4326 represents the standard WGS84 coordinate system (used by GPS/Google Maps).
     * This single column replaces both 'latitude' and 'longitude' for spatial operations.
     */
    @Column(name = "location", columnDefinition = "geometry(Point, 4326)")
    private Point location;

    @Column(name = "about", columnDefinition = "text")
    private String about;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "achievements", columnDefinition = "jsonb")
    private JsonNode achievements;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "contact_details", columnDefinition = "jsonb")
    private JsonNode contactDetails;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 255, nullable = false)
    private RecordStatus status;
}