package io.libralink.platform.wallet.exceptions;

import io.libralink.platform.common.ApplicationException;

public class InsufficientFundsException extends ApplicationException {

    public InsufficientFundsException(String message) {
        super(message, "error_insufficient_funds");
    }
}
