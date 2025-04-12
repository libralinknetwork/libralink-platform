package io.libralink.platform.agent.services;

import io.libralink.platform.security.service.TokenService;
import io.libralink.platform.wallet.integration.api.WalletClient;
import io.libralink.platform.wallet.integration.dto.BalanceDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AgentService {

    @Autowired
    private TokenService tokenService;
    @Autowired
    private WalletClient walletClient;

    public BalanceDTO getBalance(String pubKey) throws Exception {
        return walletClient.getBalance(pubKey, tokenService.issueSystemToken());
    }
}
