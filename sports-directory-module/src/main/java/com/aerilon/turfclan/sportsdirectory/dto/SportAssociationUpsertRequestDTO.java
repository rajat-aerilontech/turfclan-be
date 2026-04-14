package com.aerilon.turfclan.sportsdirectory.dto;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SportAssociationUpsertRequestDTO {

    @NotBlank(message = "selectedSportExperience is required")
    private String selectedSportExperience;

    private String sportCategory;
    private JsonNode images;

    @NotBlank(message = "Association name is required")
    private String name;

    private String shortName;
    private String board;
    private Integer membersNumber;
    private Integer foundedYear;
    private String state;
    private String locationName;
    private JsonNode mapLocation;
    private String about;
    private JsonNode achievements;
    private AssociationContactDTO contactDetails;
}
