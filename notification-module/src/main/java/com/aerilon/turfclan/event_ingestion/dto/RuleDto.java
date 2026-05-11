package com.aerilon.turfclan.event_ingestion.dto;

import com.aerilon.turfclan.event_ingestion.enums.EventType;
import lombok.Data;

@Data
public class RuleDto {

    private String sourceApp;
    private EventType eventType;
    private RuleSets ruleSets;
}
