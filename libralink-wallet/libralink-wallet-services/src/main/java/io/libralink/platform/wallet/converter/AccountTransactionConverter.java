package io.libralink.platform.wallet.converter;

import io.libralink.platform.wallet.data.entity.AccountTransaction;
import io.libralink.platform.wallet.dto.AccountTransactionDTO;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;

public final class AccountTransactionConverter {

    private AccountTransactionConverter() {}

    public static AccountTransactionDTO toDTO(AccountTransaction transaction) {
        AccountTransactionDTO dto = new AccountTransactionDTO();
        dto.setId(transaction.getId());
        dto.setAmount(transaction.getAmount());
        dto.setNote(transaction.getNote());
        dto.setStatus(transaction.getStatus());
        dto.setType(transaction.getType());
        dto.setSourceAccountId(transaction.getSourceAccountId());
        dto.setTargetAccountId(transaction.getTargetAccountId());

        dto.setChildren(transaction.getChildren().stream().map(AccountTransactionConverter::toDTO)
                .collect(Collectors.toList()));

        dto.setCreatedAt(LocalDateTime.ofEpochSecond(transaction.getCreatedAt(), 0, ZoneOffset.UTC));
        if (transaction.getUpdatedAt() != null) {
            dto.setUpdatedAt(LocalDateTime.ofEpochSecond(transaction.getUpdatedAt(), 0, ZoneOffset.UTC));
        }

        return dto;
    }

    public static List<AccountTransactionDTO> toDTOs(List<AccountTransaction> transactions) {
        return transactions.stream().map(AccountTransactionConverter::toDTO)
                .collect(Collectors.toList());
    }
}
