package io.libralink.platform.wallet.exceptions;

import io.libralink.platform.common.ApplicationException;

public class AccountNotFoundException extends ApplicationException {

    public AccountNotFoundException(String message) {
        super(message, "error_account_not_found");
    }
}
