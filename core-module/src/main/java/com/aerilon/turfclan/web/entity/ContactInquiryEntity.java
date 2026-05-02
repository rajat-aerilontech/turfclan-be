package com.aerilon.turfclan.web.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "t_contact_inquiry", schema = "turfclan_schema")
@Getter
@Setter
public class ContactInquiryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "contact_by")
    private String contactBy;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email")
    private String email;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Column(name = "subject", nullable = false)
    private String subject;

    @Column(name = "description")
    private String description;
}
