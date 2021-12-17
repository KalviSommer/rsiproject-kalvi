package com.example.rsiadvisor.error;

public class ApplicationException extends RuntimeException {

    private String message;
    public ApplicationException(String message) { this.message = message; }
    @Override
    public String getMessage() { return message; }

}
