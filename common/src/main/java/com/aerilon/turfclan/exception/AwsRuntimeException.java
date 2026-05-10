package com.aerilon.turfclan.exception;

public class AwsRuntimeException extends RuntimeException {
    public AwsRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
