package io.libralink.platform.wallet.services;

import io.libralink.platform.common.ApplicationException;
import io.libralink.platform.wallet.data.repository.AccountTransactionRepository;
import io.libralink.platform.wallet.data.repository.ECheckRepository;
import io.libralink.platform.wallet.integration.dto.IntegrationECheckDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ECheckService {

    @Autowired
    private ECheckRepository eCheckRepository;

    @Autowired
    private AccountTransactionRepository accountTransactionRepository;

    public IntegrationECheckDTO issueECheck(IntegrationECheckDTO dto) throws ApplicationException {
        return null;
    }
}
