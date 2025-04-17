package io.libralink.platform.agent.converters;

import io.libralink.client.payment.protocol.echeck.ECheck;
import io.libralink.platform.wallet.integration.dto.IntegrationECheckDTO;

import java.math.BigDecimal;
import java.util.UUID;

public final class ECheckConverter {

    private ECheckConverter() {}

    public static IntegrationECheckDTO toDTO(ECheck eCheck, String accountId, UUID envelopeId, BigDecimal feeAmount, String feeType) {
        IntegrationECheckDTO dto = new IntegrationECheckDTO();
        dto.setCheckId(eCheck.getId().toString());
        dto.setAccountId(accountId);
        dto.setEnvelopeId(envelopeId.toString());
        dto.setCreatedAt(eCheck.getCreatedAt());
        dto.setExpiresAt(eCheck.getExpiresAt());
        dto.setFaceAmount(eCheck.getFaceAmount());
        dto.setCurrency(eCheck.getCurrency().toString());
        dto.setNote(eCheck.getNote());
        dto.setPayee(eCheck.getPayee());
        dto.setPayeeProcessor(eCheck.getPayeeProcessor());
        dto.setPayer(eCheck.getPayer());
        dto.setPayerProcessor(eCheck.getPayerProcessor());
        dto.setFeeAmount(feeAmount);
        dto.setFeeType(feeType);

        return dto;
    }
}
