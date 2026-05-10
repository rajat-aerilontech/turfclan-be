package com.aerilon.turfclan.facility.entity;

import com.aerilon.turfclan.entity.BaseAuditableEntity;
import com.aerilon.turfclan.dto.S3ImageModelDto;
import com.aerilon.turfclan.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.locationtech.jts.geom.Point;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "t_facility", schema = "turfclan_schema")
@Getter
@Setter
public class FacilityEntity extends BaseAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserEntity user;

    @Column(name = "facility_name", nullable = false)
    private String facilityName;

    @Column(name = "description", nullable = false, length = 1000)
    private String description;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "facility_photos", columnDefinition = "jsonb")
    private List<S3ImageModelDto> facilityPhotos;

    @Column(name = "address_line_1")
    private String addressLine1;

    @Column(name = "address_line_2")
    private String addressLine2;

    @Column(name = "landmark")
    private String landmark;

    @Column(name = "pincode", length = 6)
    private String pincode;

    @Column(name = "city")
    private String city;

    @Column(name = "state")
    private String state;

    @Column(name = "can_be_booked")
    private Boolean canBeBooked;

    /**
     * SRID 4326 represents the standard WGS84 coordinate system (used by GPS/Google Maps).
     * This single column replaces both 'latitude' and 'longitude' for spatial operations.
     */
    @Column(name = "location", columnDefinition = "geometry(Point, 4326)")
    private Point location;

    @OneToMany(mappedBy = "facility", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SubFacilityEntity> subFacility = new ArrayList<>();
}