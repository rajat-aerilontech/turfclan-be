package com.aerilon.turfclan.partner.entity;

import com.aerilon.turfclan.dto.S3ImageModelDto;
import com.aerilon.turfclan.entity.BaseAuditableEntity;
import com.aerilon.turfclan.enums.IdProofType;
import com.aerilon.turfclan.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

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
    private UserEntity user;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "phonenumber", length = 20)
    private String phonenumber;

    @Column(name = "email_id")
    private String emailId;

    @Column(name = "designation")
    private String designation;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "profile_image_url", columnDefinition = "jsonb")
    private S3ImageModelDto profileImageUrl;

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
