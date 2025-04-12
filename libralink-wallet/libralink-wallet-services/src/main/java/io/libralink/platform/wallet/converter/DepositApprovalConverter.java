package io.libralink.platform.wallet.converter;

import io.libralink.platform.wallet.data.entity.DepositApproval;
import io.libralink.platform.wallet.dto.DepositApprovalDTO;
import io.libralink.platform.wallet.integration.dto.IntegrationDepositApprovalDTO;

public final class DepositApprovalConverter {

    private DepositApprovalConverter() {}

    public static DepositApprovalDTO toDTO(DepositApproval depositApproval) {
        DepositApprovalDTO dto = new DepositApprovalDTO();
        dto.setId(depositApproval.getId());
        dto.setAccountId(depositApproval.getAccountId());
        dto.setEnvelopeId(depositApproval.getEnvelopeId());
        dto.setCreatedAt(depositApproval.getCreatedAt());
        dto.setAmount(depositApproval.getAmount());
        dto.setStatus(depositApproval.getStatus());
        dto.setNote(depositApproval.getNote());
        dto.setPayee(depositApproval.getPayee());
        dto.setPayer(depositApproval.getPayer());
        dto.setTransactionId(depositApproval.getTransactionId());
        return dto;
    }

    public static DepositApprovalDTO toDTO(IntegrationDepositApprovalDTO depositApproval) {
        DepositApprovalDTO dto = new DepositApprovalDTO();
        dto.setEnvelopeId(depositApproval.getEnvelopeId());
        dto.setCreatedAt(depositApproval.getCreatedAt());
        dto.setAmount(depositApproval.getAmount());
        dto.setNote(depositApproval.getNote());
        dto.setPayee(depositApproval.getPayee());
        dto.setPayer(depositApproval.getPayer());
        return dto;
    }
}
