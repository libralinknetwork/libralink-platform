package io.libralink.platform.wallet.converter;

import io.libralink.platform.wallet.data.entity.Account;
import io.libralink.platform.wallet.dto.AccountDTO;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

public final class AccountConverter {

    private AccountConverter() {}

    public static AccountDTO toDTO(Account account) {
        AccountDTO dto = new AccountDTO();
        dto.setId(account.getId());
        dto.setAmount(account.getAmount());
        dto.setCurrency(account.getCurrency());
        dto.setUserId(account.getUserId());
        dto.setType(account.getType());
        dto.setUpdatedAt(LocalDateTime.ofEpochSecond(account.getUpdatedAt(), 0, ZoneOffset.UTC));
        dto.setNumber(account.getAccountNumber());

        return dto;
    }
}
