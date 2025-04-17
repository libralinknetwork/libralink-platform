package io.libralink.platform.wallet.exceptions;

import io.libralink.platform.common.ApplicationException;

public class DuplicateTransactionException extends ApplicationException {

    public DuplicateTransactionException(String message) {
        super(message, "error_duplicate_transaction");
    }
}
