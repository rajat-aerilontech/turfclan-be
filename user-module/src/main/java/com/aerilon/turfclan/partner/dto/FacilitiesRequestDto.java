package com.aerilon.turfclan.partner.dto;

import jakarta.validation.Valid;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FacilitiesRequestDto {

    @Valid
    private List<FacilityRequestDto> facilities;
}
