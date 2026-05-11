package com.aerilon.turfclan.event_ingestion.dto;

import com.aerilon.turfclan.enums.Severity;
import com.aerilon.turfclan.event_ingestion.enums.EventType;
import com.aerilon.turfclan.event_ingestion.enums.NotificationCategory;
import com.aerilon.turfclan.event_ingestion.enums.NotificationDisplayType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class RuleSets {

    @NotNull
    private String sourceApp;
    @NotNull
    private EventType eventType;
    @NotNull
    private String name;
    private Integer displaySequence;
    private String description;
    @NotNull
    private NotificationDisplayType displayType;
    @NotNull
    private Severity severity;
    @NotNull
    private NotificationCategory category;
    @NotEmpty
    private List<String> channel;
    private NotificationRuleDto notification;
    private List<String> applicableAccessType;
    private NotificationAudienceDto audience;
}
