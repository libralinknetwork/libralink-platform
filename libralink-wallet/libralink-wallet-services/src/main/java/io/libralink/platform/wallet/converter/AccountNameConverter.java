package io.libralink.platform.wallet.converter;

import io.libralink.platform.wallet.data.entity.Account;
import io.libralink.platform.wallet.data.enums.Currency;
import io.libralink.platform.wallet.dto.AccountNameDTO;

import java.util.List;
import java.util.stream.Collectors;

public final class AccountNameConverter {

    private AccountNameConverter() {}

    public static AccountNameDTO toDTO(Account account) {
        AccountNameDTO dto = new AccountNameDTO();
        dto.setId(account.getId());
        dto.setCurrency(Currency.USDC);
        dto.setType(account.getType());
        dto.setName(account.getName());

        return dto;
    }

    public static List<AccountNameDTO> toDTOs(List<Account> accounts) {
        return accounts.stream().map(AccountNameConverter::toDTO)
                .collect(Collectors.toList());
    }
}
