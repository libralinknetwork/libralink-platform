package io.libralink.platform.wallet.services;

import io.libralink.platform.common.ApplicationException;
import io.libralink.platform.wallet.converter.AccountNumberConverter;
import io.libralink.platform.wallet.converter.TransactionConverter;
import io.libralink.platform.wallet.converter.AccountConverter;
import io.libralink.platform.wallet.data.entity.Transaction;
import io.libralink.platform.wallet.data.entity.Account;
import io.libralink.platform.wallet.data.repository.TransactionRepository;
import io.libralink.platform.wallet.data.repository.AccountRepository;
import io.libralink.platform.wallet.dto.AccountNumberDTO;
import io.libralink.platform.wallet.dto.TransactionDTO;
import io.libralink.platform.wallet.dto.AccountDTO;
import io.libralink.platform.wallet.exceptions.AccountNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AccountService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountRepository accountRepository;

    public List<AccountNumberDTO> getUserAccounts(String userId) {
        List<Account> accounts = accountRepository.findByUser(userId);
        return AccountNumberConverter.toDTOs(accounts);
    }

    public AccountDTO getUserAccount(UUID userId, UUID accountId) throws ApplicationException {
        Optional<Account> accountOptional = accountRepository.findByAccountId(userId.toString(), accountId.toString());
        if (accountOptional.isPresent()) {
            return AccountConverter.toDTO(accountOptional.get());
        } else {
            throw new AccountNotFoundException(String.format("Account %s not found", accountId));
        }
    }

    public List<TransactionDTO> getAccountTransactions(UUID userId, UUID accountId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Transaction> txPage = transactionRepository.findByUserAccount(
                userId.toString(), accountId.toString(), pageable);
        return TransactionConverter.toDTOs(txPage.toList());
    }
}
