package com.aerilon.turfclan.sportsdirectory.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SportClubSummaryDTO {

    private String id;
    private String sportCategory;
    private List<String> images;
    private String name;
    private String shortName;
    private String board;
    private Integer membersNumber;
    private Integer foundedYear;
    private String state;
    private String locationName;
    private String mapLocation;
}