package io.libralink.platform.wallet.services;

import io.libralink.platform.common.ApplicationException;
import io.libralink.platform.wallet.data.entity.Account;
import io.libralink.platform.wallet.data.entity.Wallet;
import io.libralink.platform.wallet.data.enums.AccountType;
import io.libralink.platform.wallet.data.enums.Currency;
import io.libralink.platform.wallet.data.repository.AccountRepository;
import io.libralink.platform.wallet.data.repository.WalletRepository;
import io.libralink.platform.wallet.integration.dto.BalanceDTO;
import io.libralink.platform.wallet.integration.dto.CreateUserWalletDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class WalletService {

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Transactional
    public void createUserWallet(CreateUserWalletDTO request) throws ApplicationException {

        long now = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);

        /* Find Wallet, create */
        Optional<Wallet> walletOptional = walletRepository.findByUser(request.getUserId());
        if (walletOptional.isPresent()) {
            return;
        }

        Wallet wallet = new Wallet();
        wallet.setId(UUID.randomUUID().toString());
        wallet.setCurrency(Currency.USDC);
        wallet.setUserId(request.getUserId());
        wallet.setAddress(request.getAddress());
        wallet.setAlgorithm(request.getAlgorithm());
        wallet.setPublicKey(request.getPublicKey());
        walletRepository.save(wallet);

        /* Find Wallet Accounts, create */
        Account account = new Account();
        account.setId(UUID.randomUUID().toString());
        account.setName("Default");
        account.setWalletId(wallet.getId());
        account.setUpdatedAt(now);
        account.setType(AccountType.DEBIT);
        account.setAvailable(BigDecimal.ZERO);
        account.setPending(BigDecimal.ZERO);
        accountRepository.save(account);
    }

    public BalanceDTO getBalance(String pubKey) {

        BalanceDTO response = new BalanceDTO();
        response.setPubKey(pubKey);
        response.setCurrency(Currency.USDC.toString());
        response.setAvailable(BigDecimal.ZERO);
        response.setPending(BigDecimal.ZERO);

        Optional<Wallet> walletOptional = walletRepository.findByPublicKey(pubKey);
        if (walletOptional.isEmpty()) {
            return response;
        }

        List<Account> accounts = accountRepository.findByWallet(walletOptional.get().getId());
        if (accounts.isEmpty()) {
            return response;
        }

        /* Only one account by convention so far */
        Account account = accounts.get(0);
        response.setAvailable(account.getAvailable());
        response.setPending(account.getPending());

        return response;
    }
}
