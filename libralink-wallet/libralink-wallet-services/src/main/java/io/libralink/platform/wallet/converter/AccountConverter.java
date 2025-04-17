package io.libralink.platform.wallet.converter;

import io.libralink.platform.wallet.data.entity.Account;
import io.libralink.platform.wallet.data.enums.Currency;
import io.libralink.platform.wallet.dto.AccountDTO;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;

public final class AccountConverter {

    private AccountConverter() {}

    public static AccountDTO toDTO(Account account) {
        AccountDTO dto = new AccountDTO();
        dto.setId(account.getId());
        dto.setCurrency(Currency.USDC);
        dto.setType(account.getType());
        dto.setUpdatedAt(LocalDateTime.ofEpochSecond(account.getUpdatedAt(), 0, ZoneOffset.UTC));
        dto.setName(account.getName());
        dto.setAvailable(account.getAvailable());
        dto.setPending(account.getPending());
        dto.setWalletId(account.getWalletId());

        return dto;
    }

    public static List<AccountDTO> toDTOs(List<Account> accounts) {
        return accounts.stream().map(AccountConverter::toDTO)
                .collect(Collectors.toList());
    }
}
