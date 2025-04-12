package io.libralink.platform.wallet.services;

import io.libralink.platform.wallet.data.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AccountManagementService {

    @Autowired
    private AccountRepository accountRepository;

    public void createDefaultAccount(UUID userId, UUID accountNumber) {

        /* TODO: User provisioning needs to be refined */
//        List<Account> accounts = accountRepository.find(userId.toString());
//        if (accounts.isEmpty()) {
//            Account account = new Account();
//            account.setId(UUID.randomUUID().toString());
//            account.setAccountNumber(accountNumber.toString());
//            account.setUserId(userId.toString());
//            account.setAmount(BigDecimal.ZERO);
//            account.setType(AccountType.DEBIT);
//            account.setCurrency("USDC");
//            account.setUpdatedAt(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));
//
//            accountRepository.save(account);
//        }
    }
}
