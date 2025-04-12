package io.libralink.platform.wallet.converter;

import io.libralink.platform.wallet.data.entity.ECheck;
import io.libralink.platform.wallet.dto.ECheckDTO;
import io.libralink.platform.wallet.integration.dto.IntegrationECheckDTO;

public final class ECheckConverter {

    private ECheckConverter() {}

    public static ECheckDTO toDTO(ECheck eCheck) {
        ECheckDTO dto = new ECheckDTO();
        dto.setId(eCheck.getId());
        dto.setAccountId(eCheck.getAccountId());
        dto.setEnvelopeId(eCheck.getEnvelopeId());
        dto.setCreatedAt(eCheck.getCreatedAt());
        dto.setExpiresAt(eCheck.getExpiresAt());
        dto.setFaceAmount(eCheck.getFaceAmount());
        dto.setCurrency(eCheck.getCurrency().toString());
        dto.setStatus(eCheck.getStatus().toString());
        dto.setNote(eCheck.getNote());
        dto.setPayee(eCheck.getPayee());
        dto.setPayeeProcessor(eCheck.getPayeeProcessor());
        dto.setPayer(eCheck.getPayer());
        dto.setPayerProcessor(eCheck.getPayerProcessor());
        dto.setTransactionId(eCheck.getTransactionId());
        return dto;
    }

    public static ECheckDTO toDTO(IntegrationECheckDTO eCheck) {
        ECheckDTO dto = new ECheckDTO();
        dto.setEnvelopeId(eCheck.getEnvelopeId());
        dto.setCreatedAt(eCheck.getCreatedAt());
        dto.setExpiresAt(eCheck.getExpiresAt());
        dto.setFaceAmount(eCheck.getFaceAmount());
        dto.setCurrency(eCheck.getCurrency());
        dto.setNote(eCheck.getNote());
        dto.setPayee(eCheck.getPayee());
        dto.setPayeeProcessor(eCheck.getPayeeProcessor());
        dto.setPayer(eCheck.getPayer());
        dto.setPayerProcessor(eCheck.getPayerProcessor());
        return dto;
    }
}
