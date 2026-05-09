package com.aerilon.turfclan.event_ingestion.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class NotificationRuleDto {

    private List<String> data;
    @NotNull
    private String title;
    @NotNull
    private String body;
    @NotNull
    private String actionType;
    @NotNull
    private String actionUrl;
    @NotNull
    private String actionName;
}
