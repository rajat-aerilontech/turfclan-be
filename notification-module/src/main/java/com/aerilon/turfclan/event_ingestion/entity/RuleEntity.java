package com.aerilon.turfclan.event_ingestion.entity;

import com.aerilon.turfclan.entity.BaseAuditableEntity;
import com.aerilon.turfclan.event_ingestion.enums.EventType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnTransformer;

import java.util.UUID;

@Entity
@Table(name = "t_rules", schema = "turfclan_notification_schema")
@Getter
@Setter
public class RuleEntity extends BaseAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "source_app")
    private String sourceApp;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type")
    private EventType eventType;

    @Column(name = "rule_sets", columnDefinition = "jsonb")
    @ColumnTransformer(write = "?::jsonb")
    private String ruleSets;
}
