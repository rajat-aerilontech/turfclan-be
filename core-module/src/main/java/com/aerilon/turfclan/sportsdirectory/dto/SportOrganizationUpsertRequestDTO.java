package com.aerilon.turfclan.sportsdirectory.dto;

import com.aerilon.turfclan.enums.Sports;
import com.aerilon.turfclan.sportsdirectory.enums.OrganizationType;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
public class SportOrganizationUpsertRequestDTO {

    @NotNull(message = "Sport category is required")
    private Sports sportCategory;

    @NotNull(message = "Organization type is required")
    private OrganizationType organizationType;

    private List<MultipartFile> images;

    @NotBlank(message = "Organization name is required")
    @Size(max = 255, message = "Name must not exceed 255 characters")
    private String name;

    @Size(max = 100, message = "Short name must not exceed 100 characters")
    private String shortName;

    @Size(max = 255, message = "Board must not exceed 255 characters")
    private String board;

    @PositiveOrZero(message = "Members number cannot be negative")
    private Integer membersNumber;

    @Min(value = 1800, message = "Founded year is invalid")
    @Max(value = 2100, message = "Founded year is invalid")
    private Integer foundedYear;

    @Size(max = 100, message = "State must not exceed 100 characters")
    private String state;

    @Size(max = 255, message = "Location name must not exceed 255 characters")
    private String locationName;

    private String mapLocation;

    // Added for JTS Point mapping
    @Min(value = -90, message = "Latitude must be between -90 and 90")
    @Max(value = 90, message = "Latitude must be between -90 and 90")
    private Double latitude;

    @Min(value = -180, message = "Longitude must be between -180 and 180")
    @Max(value = 180, message = "Longitude must be between -180 and 180")
    private Double longitude;

    private String about;
    private String achievements;

    @Valid
    private OrganizationContactDto contactDetails;
}
