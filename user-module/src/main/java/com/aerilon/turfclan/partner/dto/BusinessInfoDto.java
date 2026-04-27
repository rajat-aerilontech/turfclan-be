package com.aerilon.turfclan.partner.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BusinessInfoDto {

    @Valid
    private BusinessDetailDto businessDetail;

    @Valid
    private BrandDetailsDto brandDetails;

    @Valid
    private HelpUsDetailDto helpUsDetail;
}
