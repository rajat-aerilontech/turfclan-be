package com.aerilon.turfclan.partner.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class SocialLinkRequestDto{
    @NotBlank(message = "Social link name is required")
    @Size(max = 50, message = "Social link name must be at most 50 characters")
    private String name;

    @NotBlank(message = "Social link is required")
    @Size(max = 255, message = "Social link must be at most 255 characters")
    private String link;

    @Size(max = 50, message = "Followers must be at most 50 characters")
    private String followers;

    private Boolean visible;
}