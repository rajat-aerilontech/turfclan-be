package com.aerilon.turfclan.user.dto;

import com.aerilon.turfclan.user.enums.Gender;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignupPersonalDTO {

    @NotBlank(message = "Sports must be selected")
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

    @NotNull(message = "Please select the gender")
    private Gender gender;

    @JsonProperty("city")
    @NotBlank(message = "Please tell us your city")
    private String city;
}
