package io.libralink.platform.wallet.integration.dto;

import java.math.BigDecimal;

public class IntegrationECheckDTO {

    private String correlationId;
    private BigDecimal faceAmount;
    private String currency;
    private String from;
    private String fromProc;
    private String to;
    private String toProc;
    private Long createdAt;
    private Long expiresAt;
    private String note;
    private String envelopeId;
    private String accountId;

    private BigDecimal feeAmount;
    private String feeType;

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    public BigDecimal getFaceAmount() {
        return faceAmount;
    }

    public void setFaceAmount(BigDecimal faceAmount) {
        this.faceAmount = faceAmount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getFromProc() {
        return fromProc;
    }

    public void setFromProc(String fromProc) {
        this.fromProc = fromProc;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getToProc() {
        return toProc;
    }

    public void setToProc(String toProc) {
        this.toProc = toProc;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public Long getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Long expiresAt) {
        this.expiresAt = expiresAt;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getEnvelopeId() {
        return envelopeId;
    }

    public void setEnvelopeId(String envelopeId) {
        this.envelopeId = envelopeId;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public BigDecimal getFeeAmount() {
        return feeAmount;
    }

    public void setFeeAmount(BigDecimal feeAmount) {
        this.feeAmount = feeAmount;
    }

    public String getFeeType() {
        return feeType;
    }

    public void setFeeType(String feeType) {
        this.feeType = feeType;
    }
}
