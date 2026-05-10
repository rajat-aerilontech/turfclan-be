package com.aerilon.turfclan.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContactInquiryDto {
    @NotBlank(message = "Contact method is required")
    private String contactBy;
    @NotBlank(message = "Name is required")
    private String name;
    @Email(message = "Invalid email format")
    private String email;
    @Pattern(regexp = "^\\d{10}$", message = "Phone number must be exactly 10 digits")
    private String phoneNumber;
    @NotBlank(message = "Subject is required")
    private String subject;
    private String description;
    @NotBlank(message = "Captcha verification is required")
    private String recaptchaToken;
}
