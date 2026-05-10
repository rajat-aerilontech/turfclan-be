package com.aerilon.turfclan.event_ingestion.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class NotificationAudienceDto {
    @NotEmpty
    private List<String> targets;
    private Boolean includeInitiator;
}
