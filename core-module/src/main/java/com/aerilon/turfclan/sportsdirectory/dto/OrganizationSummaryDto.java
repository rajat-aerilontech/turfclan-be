package com.aerilon.turfclan.sportsdirectory.dto;

import com.aerilon.turfclan.enums.Sports;
import com.aerilon.turfclan.sportsdirectory.enums.OrganizationType;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OrganizationSummaryDto {

    private String id;
    private OrganizationType organizationType;
    private Sports sportCategory;
    private List<String> images;
    private String name;
    private String shortName;
    private String board;
    private Integer membersNumber;
    private Integer foundedYear;
    private String state;
    private String locationName;
    private String mapLocation;
    // Added for Point location
    private Double latitude;
    private Double longitude;
}
