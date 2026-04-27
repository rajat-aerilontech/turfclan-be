package com.aerilon.turfclan.partner.entity;

import com.aerilon.turfclan.entity.BaseAuditableEntity;
import com.aerilon.turfclan.partner.enums.BusinessType;
import com.aerilon.turfclan.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "t_business_detail", schema = "turfclan_schema")
@Getter
@Setter
public class BusinessDetailEntity extends BaseAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserEntity userId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", referencedColumnName = "id")
    private OnboardingApplicationEntity applicationId;

    @Column(name = "business_name")
    private String businessName;

    @Enumerated(EnumType.STRING)
    @Column(name = "business_type")
    private BusinessType businessType;

    @Column(name = "gst_number", length = 20)
    private String gstNumber;

    @Column(name = "pan_number", length = 20)
    private String panNumber;

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
}
