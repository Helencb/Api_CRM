package com.helen.api_crm.exception;

public class ClientNotFoundException extends RuntimeException {

    public ClientNotFoundException(String message) {
        super(message);
    }
}
