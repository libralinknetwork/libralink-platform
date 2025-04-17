package io.libralink.platform.wallet.converter;

import io.libralink.platform.wallet.data.entity.ECheck;
import io.libralink.platform.wallet.data.enums.Currency;
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
        dto.setAccountId(eCheck.getAccountId());
        return dto;
    }

    public static ECheck toEntity(IntegrationECheckDTO eCheck) {
        ECheck entity = new ECheck();
        entity.setId(eCheck.getCheckId());
        entity.setEnvelopeId(eCheck.getEnvelopeId());
        entity.setCreatedAt(eCheck.getCreatedAt());
        entity.setExpiresAt(eCheck.getExpiresAt());
        entity.setFaceAmount(eCheck.getFaceAmount());
        entity.setCurrency(Currency.valueOf(eCheck.getCurrency()));
        entity.setNote(eCheck.getNote());
        entity.setPayee(eCheck.getPayee());
        entity.setPayeeProcessor(eCheck.getPayeeProcessor());
        entity.setPayer(eCheck.getPayer());
        entity.setPayerProcessor(eCheck.getPayerProcessor());
        entity.setAccountId(eCheck.getAccountId());
        return entity;
    }
}
