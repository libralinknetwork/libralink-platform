package io.libralink.platform.wallet.converter;

import io.libralink.platform.wallet.data.entity.Transaction;
import io.libralink.platform.wallet.dto.TransactionDTO;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;

public final class TransactionConverter {

    private TransactionConverter() {}

    public static TransactionDTO toDTO(Transaction transaction) {
        TransactionDTO dto = new TransactionDTO();
        dto.setId(transaction.getId());
        dto.setUserId(transaction.getUserId());
        dto.setAccountId(transaction.getAccountId());
        dto.setType(transaction.getType());
        dto.setToAccountId(transaction.getToAccountId());
        dto.setAmount(transaction.getAmount());
        dto.setCurrency(transaction.getCurrency());
        dto.setStatus(transaction.getStatus());
        dto.setReference(transaction.getReference());
        dto.setCreatedAt(LocalDateTime.ofEpochSecond(transaction.getCreatedAt(), 0, ZoneOffset.UTC));

        if (transaction.getUpdatedAt() != null) {
            dto.setUpdatedAt(LocalDateTime.ofEpochSecond(transaction.getUpdatedAt(), 0, ZoneOffset.UTC));
        }

        dto.setChildren(transaction.getChildren().stream().map(TransactionConverter::toDTO)
                .collect(Collectors.toList()));

        return dto;
    }

    public static List<TransactionDTO> toDTOs(List<Transaction> transactions) {
        return transactions.stream().map(TransactionConverter::toDTO)
                .collect(Collectors.toList());
    }
}
