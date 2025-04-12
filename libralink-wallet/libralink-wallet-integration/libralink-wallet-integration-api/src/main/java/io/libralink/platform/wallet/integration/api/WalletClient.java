package io.libralink.platform.wallet.integration.api;

import io.libralink.platform.common.ApplicationException;
import io.libralink.platform.wallet.integration.dto.IntegrationDepositApprovalDTO;
import io.libralink.platform.wallet.integration.dto.IntegrationECheckDTO;

public interface WalletClient {

    IntegrationECheckDTO register(IntegrationECheckDTO dto, String systemToken) throws ApplicationException;

    void register(IntegrationDepositApprovalDTO dto) throws ApplicationException;
}
