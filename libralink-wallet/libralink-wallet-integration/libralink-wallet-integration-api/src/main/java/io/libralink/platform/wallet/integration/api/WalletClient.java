package io.libralink.platform.wallet.integration.api;

import io.libralink.platform.common.ApplicationException;
import io.libralink.platform.wallet.integration.dto.BalanceDTO;
import io.libralink.platform.wallet.integration.dto.IntegrationDepositApprovalDTO;
import io.libralink.platform.wallet.integration.dto.IntegrationECheckDTO;

public interface WalletClient {

    BalanceDTO getBalance(String pubKey, String systemToken) throws ApplicationException;

    void createUserWallet(String userId, String address, String publicKey, String algorithm, String systemToken) throws ApplicationException;

    void register(IntegrationECheckDTO dto, String systemToken) throws ApplicationException;

    void register(IntegrationDepositApprovalDTO dto) throws ApplicationException;
}
