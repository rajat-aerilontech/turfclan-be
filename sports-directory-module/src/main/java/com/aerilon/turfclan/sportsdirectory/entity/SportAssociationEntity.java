package com.aerilon.turfclan.sportsdirectory.entity;

import com.aerilon.turfclan.entity.BaseAuditableEntity;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "t_sport_associations", schema = "turfclan_schema", uniqueConstraints = {
        @UniqueConstraint(columnNames = "name")})
@Getter
@Setter
public class SportAssociationEntity extends BaseAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "sport_category", length = 100)
    private String sportCategory;

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

    @Column(name = "about", columnDefinition = "text")
    private String about;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "achievements", columnDefinition = "jsonb")
    private JsonNode achievements;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "contact_details", columnDefinition = "jsonb")
    private JsonNode contactDetails;
}
