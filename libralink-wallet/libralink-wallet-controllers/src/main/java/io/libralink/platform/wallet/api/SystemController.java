package io.libralink.platform.wallet.api;

import io.libralink.platform.wallet.integration.dto.BalanceDTO;
import io.libralink.platform.wallet.integration.dto.IntegrationECheckDTO;
import io.libralink.platform.wallet.services.ECheckService;
import io.libralink.platform.wallet.services.WalletService;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Api(tags = "Internal")
@RestController
public class SystemController {

    private static final Logger LOG = LoggerFactory.getLogger(SystemController.class);

    @Autowired
    private ECheckService eCheckIssueService;

    @Autowired
    private WalletService walletService;

    @PreAuthorize("hasAuthority('SYSTEM')")
    @PostMapping(value = "/internal/wallet/issue-e-check", produces = "application/json")
    public IntegrationECheckDTO issueECheck(@RequestBody IntegrationECheckDTO eCheckDTO) throws Exception {
        LOG.info("E-Check Issue request received");

        return eCheckIssueService.issueECheck(eCheckDTO);
    }

    @PreAuthorize("hasAuthority('SYSTEM')")
    @PostMapping(value = "/internal/wallet/balance", produces = "application/json")
    public BalanceDTO getBalance(@RequestBody BalanceDTO balanceDTO) throws Exception {
        return walletService.getBalance(balanceDTO.getPubKey());
    }
}
