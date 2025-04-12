package io.libralink.platform.wallet.dto;

import io.libralink.platform.wallet.data.enums.AccountType;
import io.libralink.platform.wallet.data.enums.Currency;

public class AccountNameDTO {

    private String id;
    private String name;
    private AccountType type;
    private Currency currency;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AccountType getType() {
        return type;
    }

    public void setType(AccountType type) {
        this.type = type;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }
}
