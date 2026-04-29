package com.aerilon.turfclan.user.dto;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DashboardResponseDTO {

    private String userId;
    private String selectedSportExperience;
    private JsonNode userPerformance;
    private JsonNode sportSpecificDetail;
}
