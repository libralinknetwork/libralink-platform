package io.libralink.platform.wallet.converter;

import io.libralink.platform.wallet.data.entity.Account;
import io.libralink.platform.wallet.dto.AccountNumberDTO;

import java.util.List;
import java.util.stream.Collectors;

public final class AccountNumberConverter {

    private AccountNumberConverter() {}

    public static AccountNumberDTO toDTO(Account account) {
        AccountNumberDTO dto = new AccountNumberDTO();
        dto.setId(account.getId());
        dto.setCurrency(account.getCurrency());
        dto.setType(account.getType());
        dto.setNumber(account.getAccountNumber());

        return dto;
    }

    public static List<AccountNumberDTO> toDTOs(List<Account> accounts) {
        return accounts.stream().map(AccountNumberConverter::toDTO)
                .collect(Collectors.toList());
    }
}
