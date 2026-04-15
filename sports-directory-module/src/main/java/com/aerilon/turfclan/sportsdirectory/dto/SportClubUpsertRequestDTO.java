package com.aerilon.turfclan.sportsdirectory.dto;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SportClubUpsertRequestDTO {

    private String sportCategory;
    private List<String> images;

    @NotBlank(message = "Club name is required")
    private String name;

    private String shortName;
    private String board;
    private Integer membersNumber;
    private Integer foundedYear;
    private String state;
    private String locationName;
    private String mapLocation;
    private String about;
    private JsonNode achievements;
    private AssociationContactDTO contactDetails;
}