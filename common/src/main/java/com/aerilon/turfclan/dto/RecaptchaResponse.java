package com.aerilon.turfclan.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record RecaptchaResponse(
        boolean success,
        float score,
        String action,
        String hostname,
        @JsonProperty("challenge_ts") String challengeTs,
        @JsonProperty("error-codes") List<String> errorCodes
) {}