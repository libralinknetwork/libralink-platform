package io.libralink.platform.wallet.services;

import io.libralink.platform.common.ApplicationException;
import io.libralink.platform.wallet.converter.AccountNameConverter;
import io.libralink.platform.wallet.converter.AccountTransactionConverter;
import io.libralink.platform.wallet.converter.AccountConverter;
import io.libralink.platform.wallet.data.entity.AccountTransaction;
import io.libralink.platform.wallet.data.entity.Account;
import io.libralink.platform.wallet.data.repository.AccountTransactionRepository;
import io.libralink.platform.wallet.data.repository.AccountRepository;
import io.libralink.platform.wallet.dto.AccountNameDTO;
import io.libralink.platform.wallet.dto.AccountTransactionDTO;
import io.libralink.platform.wallet.dto.AccountDTO;
import io.libralink.platform.wallet.exceptions.AccountNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AccountService {

    @Autowired
    private AccountTransactionRepository transactionRepository;

    @Autowired
    private AccountRepository accountRepository;

    public List<AccountNameDTO> getUserAccounts(String userId) {

//        String walletId = ...
//        List<Account> accounts = accountRepository.findByWallet(userId);
//        return AccountNameConverter.toDTOs(accounts);

        return AccountNameConverter.toDTOs(new ArrayList<>());
    }

    public AccountDTO getUserAccount(UUID userId, UUID accountId) throws ApplicationException {
        Optional<Account> accountOptional = accountRepository.findByAccountId(userId.toString(), accountId.toString());
        if (accountOptional.isPresent()) {
            return AccountConverter.toDTO(accountOptional.get());
        } else {
            throw new AccountNotFoundException(String.format("Account %s not found", accountId));
        }
    }

    public List<AccountTransactionDTO> getAccountTransactions(UUID accountId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<AccountTransaction> txPage = transactionRepository.findByAccount(accountId.toString(), pageable);
        return AccountTransactionConverter.toDTOs(txPage.toList());
    }
}
