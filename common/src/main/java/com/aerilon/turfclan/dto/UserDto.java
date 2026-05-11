package com.aerilon.turfclan.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDto {
    private String userEmail;
    private String languageIsoCode;
    private String countryIsoCode;
    private String userRole;
}
