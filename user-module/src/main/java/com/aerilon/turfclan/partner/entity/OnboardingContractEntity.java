package com.aerilon.turfclan.partner.entity;

import com.aerilon.turfclan.partner.enums.SignatureType;
import com.aerilon.turfclan.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "t_partner_contract", schema = "turfclan_schema")
@Getter
@Setter
public class OnboardingContractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", referencedColumnName = "id", nullable = false)
    private OnboardingApplicationEntity application;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserEntity userId;

    @Column(name = "agreement_version", nullable = false)
    private String agreementVersion;

    @Column(name = "is_agreed", nullable = false)
    private Boolean isAgreed;

    @Enumerated(EnumType.STRING)
    @Column(name = "signature_type", nullable = false)
    private SignatureType signatureType;

    @Column(name = "typed_signature_name")
    private String typedSignatureName;

    @Column(name = "uploaded_signature_url", length = 1000)
    private String uploadedSignatureUrl;

    @Column(name = "ip_address", length = 45, nullable = false)
    private String ipAddress;

    @Column(name = "signed_at", nullable = false)
    private LocalDateTime signedAt;
}