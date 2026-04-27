package com.aerilon.turfclan.partner.entity;

import com.aerilon.turfclan.entity.BaseAuditableEntity;
import com.aerilon.turfclan.partner.enums.OnboardApplicationStatus;
import com.aerilon.turfclan.partner.enums.OnboardStep;
import com.aerilon.turfclan.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "t_partner_application", schema = "turfclan_schema")
@Getter
@Setter
public class OnboardingApplicationEntity extends BaseAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserEntity userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "onboard_application_status")
    private OnboardApplicationStatus OnboardApplicationStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "current_step")
    private OnboardStep currentStep;

    @Column(name = "is_submitted")
    private Boolean isSubmitted;
}
