package com.aerilon.turfclan.event_ingestion.dto;

import com.aerilon.turfclan.enums.Severity;
import com.aerilon.turfclan.event_ingestion.enums.EventType;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
public class EventDto {
    private UUID eventId;
    private EventType eventType;
    private String sourceApp;
    private String eventCreatedAt;
    private String initiatorId;
    private String initiatorType;
    private Severity severity;
    private Map<String, String> data;
    private List<String> failedChannel;
}
