package io.libralink.platform.agent.exceptions;

import io.libralink.platform.common.ApplicationException;

public class EnvelopeNotFoundException extends ApplicationException {

    public EnvelopeNotFoundException(String message) {
        super(message, "envelope_not_found");
    }
}
