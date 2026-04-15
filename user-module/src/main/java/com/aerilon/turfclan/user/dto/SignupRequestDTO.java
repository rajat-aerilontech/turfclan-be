package com.aerilon.turfclan.user.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignupRequestDTO {

    @Valid
    @NotNull(message = "Personal details must not be null")
    private SignupPersonalDTO personal;

    @Valid
    @NotNull(message = "Sport details must not be null")
    private SignupSportDTO sport;
}
