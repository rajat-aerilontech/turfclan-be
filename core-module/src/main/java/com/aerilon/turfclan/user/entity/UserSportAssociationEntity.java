package com.aerilon.turfclan.user.entity;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Entity
@Table(name = "r_association_user_sport", schema = "turfclan_schema")
@Getter
@Setter
public class UserSportAssociationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "sport_id", nullable = false)
    private UUID sportId;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "sport_profile", columnDefinition = "jsonb")
    private JsonNode sportProfile;
}
