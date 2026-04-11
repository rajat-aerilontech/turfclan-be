package com.aerilon.turfclan.user.entity;

import com.aerilon.turfclan.user.enums.UserStatus;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "t_users", schema = "turfclan_schema", uniqueConstraints = {
        @UniqueConstraint(columnNames = "username"),
        @UniqueConstraint(columnNames = "user_email"),
        @UniqueConstraint(columnNames = "phone_number")})
@Getter
@Setter
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "username", nullable = false, unique = true)
    private String userName;

    @Column(name = "user_email", length = 255, unique = true)
    private String userEmail;

    @Column(name = "first_name", length = 100)
    private String firstName;

    @Column(name = "last_name", length = 100)
    private String lastName;

    @Column(name = "phone_country_code", length = 10)
    private String phoneCountryCode;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Column(name = "profile_picture_url", length = 255)
    private String profilePictureUrl;

    @Column(name = "bio", length = 500)
    private String bio;

    @Column(name = "date_of_birth", length = 20)
    private String dateOfBirth;

    @Column(name = "gender", length = 20)
    private String gender;

    @Column(name = "location", length = 255)
    private String location;

    @Column(name = "sport", length = 500)
    private String sport;

    @Column(name = "is_verified")
    private boolean isVerified;

    @Column(name = "country_iso_code", length = 10)
    private String countryIsoCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 255, nullable = false)
    private UserStatus status;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdBy;

    @LastModifiedDate
    @Column(name = "updated_at", insertable = false)
    private LocalDateTime updatedBy;
}

