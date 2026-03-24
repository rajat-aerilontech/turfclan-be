package com.aerilon.turfclan.user.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignupRequestDTO {

    @NotBlank(message = "First name must not be blank")
    private String firstName;

    @NotBlank(message = "Last name must not be blank")
    private String lastName;

    /** Optional */
    @JsonAlias({"email", "user_email"})
    private String email;

    @NotBlank(message = "Location must not be blank")
    private String location;

    @NotBlank(message = "Gender must not be blank")
    private String gender;

    @NotBlank(message = "Date of birth must not be blank")
    private String dateOfBirth;

    @NotBlank(message = "Sport must not be blank")
    private String sport;
}
