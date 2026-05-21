package com.aerilon.turfclan.sportsdirectory.dto;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SportOrganizationDetailDto {

    private String about;
    private JsonNode achievements;
    private OrganizationContactDto contactDetails;
}
