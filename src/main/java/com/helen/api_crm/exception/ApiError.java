package com.helen.api_crm.exception;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ApiError {

    private int status;
    private String error;
    private String message;
    private LocalDateTime timestamp;

    public ApiError(int status, String error, String message) {
        this.status = status;
        this.error = error;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }
}
