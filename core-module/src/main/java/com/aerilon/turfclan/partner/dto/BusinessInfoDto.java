package com.aerilon.turfclan.partner.dto;

import jakarta.validation.Valid;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BusinessInfoDto {

    @Valid
    private BusinessDetailRequestDto businessDetail;

    @Valid
    private BrandDetailsRequestDto brandDetails;

    @Valid
    private HelpUsDetailRequestDto helpUsDetail;

    @Valid
    private List<SocialLinkRequestDto> socialLinks;
}
