package com.aerilon.turfclan.partner.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SocialLinkDto {
    private UUID id;
    private String name;
    private String link;
    private String followers;
    private Boolean visible;
}