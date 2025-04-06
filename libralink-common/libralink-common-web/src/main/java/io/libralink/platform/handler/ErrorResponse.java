package io.libralink.platform.handler;

public class ErrorResponse {

    private final String message;
    private final String code;

    public ErrorResponse(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
