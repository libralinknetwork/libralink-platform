package io.libralink.platform.wallet.services;

import io.libralink.platform.wallet.data.entity.Account;
import io.libralink.platform.wallet.data.entity.Wallet;
import io.libralink.platform.wallet.data.enums.Currency;
import io.libralink.platform.wallet.data.repository.AccountRepository;
import io.libralink.platform.wallet.data.repository.WalletRepository;
import io.libralink.platform.wallet.integration.dto.BalanceDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class WalletService {

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private AccountRepository accountRepository;

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
