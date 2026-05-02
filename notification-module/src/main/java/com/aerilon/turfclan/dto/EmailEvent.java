package com.aerilon.turfclan.dto;

import java.util.Map;

public record EmailEvent(
        String recipient,
        String subject,
        String templateName,
        Map<String, Object> variables
) {}