package io.libralink.platform.agent.converters;

import io.libralink.client.payment.proto.Libralink;
import io.libralink.platform.wallet.integration.dto.IntegrationECheckDTO;

import java.math.BigDecimal;

public final class ECheckConverter {

    private ECheckConverter() {}

    public static IntegrationECheckDTO toDTO(Libralink.ECheck eCheck, String accountId, String envelopeId, String feeAmount, String feeType) {
        IntegrationECheckDTO dto = new IntegrationECheckDTO();
        dto.setCorrelationId(eCheck.getCorrelationId());
        dto.setAccountId(accountId);
        dto.setEnvelopeId(envelopeId);
        dto.setCreatedAt(eCheck.getCreatedAt());
        dto.setExpiresAt(eCheck.getExpiresAt());
        dto.setFaceAmount(new BigDecimal(eCheck.getFaceAmount()));
        dto.setCurrency(eCheck.getCurrency());
        dto.setNote(eCheck.getNote());
        dto.setTo(eCheck.getTo());
        dto.setToProc(eCheck.getToProc());
        dto.setFrom(eCheck.getFrom());
        dto.setFromProc(eCheck.getFromProc());
        dto.setFeeAmount(new BigDecimal(feeAmount));
        dto.setFeeType(feeType);

        return dto;
    }
}
