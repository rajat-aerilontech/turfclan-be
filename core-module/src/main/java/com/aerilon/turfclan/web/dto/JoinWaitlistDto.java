package com.aerilon.turfclan.web.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JoinWaitlistDto {
    @NotBlank(message = "Email or phone number is required")
    private String email_phone;
    @NotBlank(message = "Captcha verification is required")
    private String recaptchaToken;
}
