package io.libralink.platform.wallet.services;

import io.libralink.platform.common.ApplicationException;
import io.libralink.platform.wallet.data.entity.Account;
import io.libralink.platform.wallet.data.entity.AccountTransaction;
import io.libralink.platform.wallet.data.entity.ECheck;
import io.libralink.platform.wallet.data.enums.CheckStatus;
import io.libralink.platform.wallet.data.enums.TransactionStatus;
import io.libralink.platform.wallet.data.enums.TransactionType;
import io.libralink.platform.wallet.data.repository.AccountRepository;
import io.libralink.platform.wallet.data.repository.AccountTransactionRepository;
import io.libralink.platform.wallet.data.repository.ECheckRepository;
import io.libralink.platform.wallet.exceptions.DuplicateTransactionException;
import io.libralink.platform.wallet.exceptions.InsufficientFundsException;
import io.libralink.platform.wallet.integration.dto.IntegrationECheckDTO;
import io.libralink.platform.wallet.utils.BalanceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;

import static io.libralink.platform.wallet.converter.ECheckConverter.toDTO;
import static io.libralink.platform.wallet.converter.ECheckConverter.toEntity;

@Service
public class ECheckService {

    private static final Logger LOG = LoggerFactory.getLogger(ECheckService.class);

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ECheckRepository eCheckRepository;

    @Autowired
    private AccountTransactionRepository accountTransactionRepository;

    @Transactional
    public void issueECheck(IntegrationECheckDTO dto) throws ApplicationException {

        long now = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);

        Optional<AccountTransaction> transactionOptional = accountTransactionRepository.findByEnvelope(dto.getEnvelopeId());
        if (transactionOptional.isPresent()) {
            throw new DuplicateTransactionException("E-Check already issued");
        }

        Account account = accountRepository.findById(dto.getAccountId()).get();
        BigDecimal blockingAmount = BalanceUtils.getTotalAmount(dto.getFaceAmount(), dto.getFeeAmount(), dto.getFeeType());

        if (account.getAvailable().compareTo(blockingAmount) < 0) {
            throw new InsufficientFundsException("Not enough 'available' balance");
        }

        /* Block Payer funds */
        account.setAvailable(account.getAvailable().subtract(blockingAmount));
        account.setPending(account.getPending().add(blockingAmount));
        accountRepository.save(account);

        AccountTransaction transaction = new AccountTransaction();
        transaction.setId(UUID.randomUUID().toString());
        transaction.setCreatedAt(now);
        transaction.setUpdatedAt(now);
        transaction.setNote(dto.getNote());
        transaction.setStatus(TransactionStatus.PENDING);
        transaction.setAmount(dto.getFaceAmount());
        transaction.setSourceAccountId(dto.getAccountId());
        transaction.setType(TransactionType.PAYMENT);
        transaction.setEnvelopeId(dto.getEnvelopeId());
        transaction = accountTransactionRepository.save(transaction);

        ECheck eCheck = toEntity(dto);
        eCheck.setStatus(CheckStatus.ISSUED);
        eCheck.setTransactionId(transaction.getId());
        eCheckRepository.save(eCheck);
    }
}
