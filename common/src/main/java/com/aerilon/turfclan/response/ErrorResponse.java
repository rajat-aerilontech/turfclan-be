package com.aerilon.turfclan.response;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class ErrorResponse {
    private int status;
    private String message;
    private long timestamp;
    private Map<String, String> fields;

    public ErrorResponse(int status, String message, long timestamp) {
        this.status = status;
        this.message = message;
        this.timestamp = timestamp;
    }

    public ErrorResponse(int status, String message, long timestamp, Map<String, String> fields) {
        this.status = status;
        this.message = message;
        this.timestamp = timestamp;
        this.fields = fields;
    }
}
