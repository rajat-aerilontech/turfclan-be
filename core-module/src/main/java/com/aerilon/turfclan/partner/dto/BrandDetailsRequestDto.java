package com.aerilon.turfclan.partner.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BrandDetailsRequestDto {

    @NotBlank(message = "Brand Name is required")
    private String brandName;
    @NotBlank(message = "Tag Line is required")
    private String tagline;
    @NotBlank(message = "Brand Logo is required")
    private String brandLogoUrl;
    @NotEmpty(message = "At least one banner image is required")
    private List<String> bannerImageUrls;
    @NotBlank(message = "Short Description is required")
    private String description;
    @NotBlank(message = "Long Description is required")
    private String longDescription;
    private String brandWebsite;

    @Valid
    private List<SocialLinkRequestDto> socialLinks;
}
