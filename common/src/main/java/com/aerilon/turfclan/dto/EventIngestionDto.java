package com.aerilon.turfclan.dto;

import com.aerilon.turfclan.enums.Severity;import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class EventIngestionDto {
    private String eventId;
    private String eventType;
    private String sourceApp;
    private String eventCreatedAt;
    private String initiatorId;
    private String initiatorType;
    private Severity severity;
    private Map<String, String> data;
}
