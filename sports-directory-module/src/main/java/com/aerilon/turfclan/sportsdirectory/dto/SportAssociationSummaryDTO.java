package com.aerilon.turfclan.sportsdirectory.dto;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SportAssociationSummaryDTO {

    private String id;
    private String sportCategory;
    private JsonNode images;
    private String name;
    private String shortName;
    private String board;
    private Integer membersNumber;
    private Integer foundedYear;
    private String state;
    private String locationName;
    private JsonNode mapLocation;
}
