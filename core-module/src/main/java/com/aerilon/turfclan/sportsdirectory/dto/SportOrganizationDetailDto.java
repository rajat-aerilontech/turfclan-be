package com.aerilon.turfclan.sportsdirectory.dto;

import com.aerilon.turfclan.dto.S3ImageResponseDto;
import com.aerilon.turfclan.enums.Sports;
import com.aerilon.turfclan.sportsdirectory.enums.OrganizationType;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SportOrganizationDetailDto {
    private Sports sportCategory;
    private OrganizationType organizationType;
    private List<S3ImageResponseDto> images;
    private String name;
    private String shortName;
    private String board;
    private Integer membersNumber;
    private Integer foundedYear;
    private String state;
    private String locationName;
    private String mapLocation;
    private Double latitude;
    private Double longitude;
    private String about;
    private JsonNode achievements;
    private OrganizationContactDto contactDetails;
}
