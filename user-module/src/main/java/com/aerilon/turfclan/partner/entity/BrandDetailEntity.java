package com.aerilon.turfclan.partner.entity;

import com.aerilon.turfclan.entity.BaseAuditableEntity;
import com.aerilon.turfclan.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.locationtech.jts.geom.Point;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "t_brand_detail", schema = "turfclan_schema")
@Getter
@Setter
public class BrandDetailEntity extends BaseAuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserEntity user;

    @Column(name = "brand_name")
    private String brandName;

    @Column(name = "tagline")
    private String tagline;

    @Column(name = "brand_logo_url")
    private String brandLogoUrl;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "banner_url", columnDefinition = "text[]")
    private Set<String> bannerImageUrls = new HashSet<>();

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "long_description", length = 1000)
    private String longDescription;

    @Column(name = "brand_website")
    private String brandWebsite;

    @Column(name = "instagram_page")
    private String instagramPage;

    @Column(name = "youtube_page")
    private String youtubePage;

    /**
     * SRID 4326 represents the standard WGS84 coordinate system (used by GPS/Google Maps).
     * This single column replaces both 'latitude' and 'longitude' for spatial operations.
     */
    @Column(name = "location", columnDefinition = "geometry(Point, 4326)")
    private Point location;
}
