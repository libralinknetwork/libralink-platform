package io.libralink.platform.wallet.api;

import io.libralink.platform.security.common.principal.LibralinkUser;
import io.libralink.platform.wallet.dto.AccountDTO;
import io.libralink.platform.wallet.dto.AccountTransactionDTO;
import io.libralink.platform.wallet.services.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
public class WalletController {

    @Autowired
    private AccountService accountService;

    @PreAuthorize("hasAuthority('USER')")
    @GetMapping("/wallet/accounts")
    public List<AccountDTO> getAccounts(@AuthenticationPrincipal LibralinkUser principal) {
        return accountService.getUserAccounts(principal.getUsername());
    }

    @PreAuthorize("hasAuthority('USER')")
    @GetMapping("/wallet/accounts/{accountId}/transactions")
    public List<AccountTransactionDTO> getTransactions(
            @PathVariable(name = "accountId") UUID accountId,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @AuthenticationPrincipal LibralinkUser principal) {

        return accountService.getAccountTransactions(accountId, page, size);
    }
}
