package com.aerilon.turfclan.partner.entity;

import com.aerilon.turfclan.entity.BaseAuditableEntity;
import com.aerilon.turfclan.enums.IdProofType;
import com.aerilon.turfclan.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "t_partner_detail", schema = "turfclan_schema")
@Getter
@Setter
public class PartnerDetailEntity extends BaseAuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserEntity userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", referencedColumnName = "id")
    private OnboardingApplicationEntity application;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "phonenumber", length = 20)
    private String phonenumber;

    @Column(name = "email_id")
    private String emailId;

    @Column(name = "designation")
    private String designation;

    @Column(name = "profile_image_url", length = 1000)
    private String profileImageUrl;

    @Column(name = "aadhar_number", length = 20)
    private String aadharNumber;

    @Column(name = "pan_number", length = 20)
    private String panNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "id_proof_type")
    private IdProofType idProofType;

    @Column(name = "id_document_url", length = 1000)
    private String idDocumentUrl;
}
