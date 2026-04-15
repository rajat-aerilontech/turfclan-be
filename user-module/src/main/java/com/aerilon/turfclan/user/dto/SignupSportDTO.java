package com.aerilon.turfclan.user.dto;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashMap;
import java.util.Map;

@Getter
@Setter
public class SignupSportDTO {

    @NotBlank(message = "Role must not be blank")
    private String role;

    @NotBlank(message = "Experience must not be blank")
    private String experience;

    private final Map<String, Object> additionalFields = new LinkedHashMap<>();

    @JsonAnySetter
    public void setAdditionalField(String key, Object value) {
        additionalFields.put(key, value);
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalFields() {
        return additionalFields;
    }
}
