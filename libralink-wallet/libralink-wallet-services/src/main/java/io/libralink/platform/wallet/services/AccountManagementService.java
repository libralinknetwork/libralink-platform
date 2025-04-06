package io.libralink.platform.wallet.services;

import io.libralink.platform.wallet.data.entity.Account;
import io.libralink.platform.wallet.data.enums.AccountType;
import io.libralink.platform.wallet.data.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

@Service
public class AccountManagementService {

    @Autowired
    private AccountRepository accountRepository;

    public void createDefaultAccount(UUID userId, UUID accountNumber) {

        List<Account> accounts = accountRepository.findByUser(userId.toString());
        if (accounts.isEmpty()) {
            Account account = new Account();
            account.setId(UUID.randomUUID().toString());
            account.setAccountNumber(accountNumber.toString());
            account.setUserId(userId.toString());
            account.setAmount(BigDecimal.ZERO);
            account.setType(AccountType.DEBIT);
            account.setCurrency("USDC");
            account.setUpdatedAt(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));

            accountRepository.save(account);
        }
    }
}
