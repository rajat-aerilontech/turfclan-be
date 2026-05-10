package com.aerilon.turfclan.web.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PartnerWithUsDto {
    @NotBlank(message = "Name is required")
    private String name;
    @NotBlank(message = "Brand name is required")
    private String brandName;
    @NotBlank(message = "Email or phone number is required")
    private String email_phone;
    private String description;
    @NotBlank(message = "Captcha verification is required")
    private String recaptchaToken;
}
