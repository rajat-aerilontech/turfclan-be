package com.aerilon.turfclan.partner.entity;

import com.aerilon.turfclan.entity.BaseAuditableEntity;
import com.aerilon.turfclan.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "t_bank_detail", schema = "turfclan_schema")
@Getter
@Setter
public class BankDetailEntity extends BaseAuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserEntity userId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", referencedColumnName = "id")
    private OnboardingApplicationEntity applicationId;

    @Column(name = "account_holder_name", nullable = false)
    private String accountHolderName;

    @Column(name = "account_number", nullable = false)
    private String accountNumber;

    @Column(name = "bank_name", nullable = false)
    private String bankName;

    @Column(name = "ifsc_code", nullable = false,length = 20)
    private String ifscCode;

    @Column(name = "branch_name", nullable = false)
    private String branchName;

    @Column(name = "cancelled_cheque_url", nullable = false, length = 1000)
    private String cancelledChequeUrl;
}
