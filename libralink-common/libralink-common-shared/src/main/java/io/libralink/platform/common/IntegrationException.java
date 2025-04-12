package io.libralink.platform.common;

public class IntegrationException extends ApplicationException {

    public IntegrationException(String message) {
        super(message, "error_integration_failed");
    }
}
