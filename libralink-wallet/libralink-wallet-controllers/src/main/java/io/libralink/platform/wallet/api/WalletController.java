package io.libralink.platform.wallet.api;

import io.libralink.platform.common.ApplicationException;
import io.libralink.platform.wallet.dto.AccountNameDTO;
import io.libralink.platform.wallet.dto.AccountTransactionDTO;
import io.libralink.platform.wallet.dto.AccountDTO;
import io.libralink.platform.wallet.services.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
public class WalletController {

    @Autowired
    private AccountService accountService;

    @PreAuthorize("hasAuthority('USER')")
    @GetMapping("/wallet/accounts")
    public List<AccountNameDTO> getAccounts(@AuthenticationPrincipal Principal principal) {
//        return accountService.getUserAccounts(principal.getUserId());

        return new ArrayList<>();
    }

    @PreAuthorize("hasAuthority('USER')")
    @GetMapping("/wallet/accounts/{accountId}")
    public AccountDTO getAccount(
            @PathVariable(name = "accountId") UUID accountId,
            @AuthenticationPrincipal Principal principal) throws ApplicationException {

//        return accountService.getUserAccount(UUID.fromString(principal.getUserId()), accountId);
        return null;
    }

    @PreAuthorize("hasAuthority('USER')")
    @GetMapping("/wallet/accounts/{accountId}/transactions")
    public List<AccountTransactionDTO> getTransactions(
            @PathVariable(name = "accountId") UUID accountId,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @AuthenticationPrincipal Principal principal) {

//        return accountService.getAccountTransactions(UUID.fromString(principal.getUserId()), accountId, page, size);
        return new ArrayList<>();
    }
}
