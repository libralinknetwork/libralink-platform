package io.libralink.platform.wallet.services;

import io.libralink.platform.wallet.converter.AccountConverter;
import io.libralink.platform.wallet.converter.AccountTransactionConverter;
import io.libralink.platform.wallet.data.entity.AccountTransaction;
import io.libralink.platform.wallet.data.entity.Account;
import io.libralink.platform.wallet.data.entity.Wallet;
import io.libralink.platform.wallet.data.repository.AccountTransactionRepository;
import io.libralink.platform.wallet.data.repository.AccountRepository;
import io.libralink.platform.wallet.data.repository.WalletRepository;
import io.libralink.platform.wallet.dto.AccountDTO;
import io.libralink.platform.wallet.dto.AccountTransactionDTO;
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

    @Autowired
    private WalletRepository walletRepository;

    public List<AccountDTO> getUserAccounts(String userId) {

        Optional<Wallet> walletOptional = walletRepository.findByUser(userId);
        if (walletOptional.isEmpty()) {
            return new ArrayList<>();
        }

        List<Account> accounts = accountRepository.findByWallet(walletOptional.get().getId());
        return AccountConverter.toDTOs(accounts);
    }

    public List<AccountTransactionDTO> getAccountTransactions(UUID accountId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<AccountTransaction> txPage = transactionRepository.findByAccount(accountId.toString(), pageable);
        return AccountTransactionConverter.toDTOs(txPage.toList());
    }
}
