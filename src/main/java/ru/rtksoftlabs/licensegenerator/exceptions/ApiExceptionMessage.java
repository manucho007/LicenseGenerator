package ru.rtksoftlabs.licensegenerator.exceptions;

public class ApiExceptionMessage {
    private String message;

    public ApiExceptionMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
