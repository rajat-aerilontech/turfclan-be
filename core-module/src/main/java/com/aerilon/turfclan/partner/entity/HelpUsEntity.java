package com.aerilon.turfclan.partner.entity;

import com.aerilon.turfclan.entity.BaseAuditableEntity;
import com.aerilon.turfclan.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "t_help_us_detail", schema = "turfclan_schema")
@Getter
@Setter
public class HelpUsEntity extends BaseAuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserEntity user;

    @Column(name = "referral_source")
    private String referralSource; // How did you hear about us?

    @Column(name = "program_understanding", length = 500)
    private String programUnderstanding;

    @Column(name = "reason_to_join")
    private String reasonToJoin;

    @Column(name = "is_actively_involved")
    private String activelyInvolved; // Day-to-day operations

    @Column(name = "time_commitment")
    private String timeCommitment;

    @Column(name = "is_associated_with_employee")
    private Boolean isAssociatedWithEmployee;

    @Column(name = "total_partners_count")
    private Integer totalPartnersCount;
}
