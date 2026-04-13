package com.aerilon.turfclan.user.dto;

import com.aerilon.turfclan.user.enums.Gender;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignupPersonalDTO {

    private String sports;

    @JsonProperty("first_name")
    @NotBlank(message = "First name must not be blank")
    private String firstName;

    @JsonProperty("last_name")
    @NotBlank(message = "Last name must not be blank")
    private String lastName;

    private String email;

    @JsonProperty("dob")
    @NotBlank(message = "Date of birth must not be blank")
    private String dob;

    @NotNull(message = "Gender must not be null")
    private Gender gender;

    @JsonProperty("city")
    @NotBlank(message = "City must not be blank")
    private String city;
}
