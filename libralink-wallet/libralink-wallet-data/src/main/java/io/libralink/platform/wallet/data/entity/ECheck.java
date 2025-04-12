package io.libralink.platform.wallet.data.entity;

import io.libralink.platform.wallet.data.enums.CheckStatus;
import io.libralink.platform.wallet.data.enums.Currency;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "e_check")
public class ECheck {

    @Id
    @Column(name = "check_id")
    private String id;

    @Column(name = "account_id")
    private String accountId;

    @Column(name = "face_amount")
    private BigDecimal faceAmount;

    @Enumerated(EnumType.STRING)
    private CheckStatus status;

    @Enumerated(EnumType.STRING)
    private Currency currency;

    private String payer;

    @Column(name = "payer_processor")
    private String payerProcessor;

    private String payee;

    @Column(name = "payee_processor")
    private String payeeProcessor;

    @Column(name = "created_at")
    private Long createdAt;

    @Column(name = "expires_at")
    private Long expiresAt;

    private String note;

    @Column(name = "envelope_id")
    private String envelopeId;

    @Column(name = "tx_id")
    private String transactionId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public BigDecimal getFaceAmount() {
        return faceAmount;
    }

    public void setFaceAmount(BigDecimal faceAmount) {
        this.faceAmount = faceAmount;
    }

    public CheckStatus getStatus() {
        return status;
    }

    public void setStatus(CheckStatus status) {
        this.status = status;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public String getPayer() {
        return payer;
    }

    public void setPayer(String payer) {
        this.payer = payer;
    }

    public String getPayerProcessor() {
        return payerProcessor;
    }

    public void setPayerProcessor(String payerProcessor) {
        this.payerProcessor = payerProcessor;
    }

    public String getPayee() {
        return payee;
    }

    public void setPayee(String payee) {
        this.payee = payee;
    }

    public String getPayeeProcessor() {
        return payeeProcessor;
    }

    public void setPayeeProcessor(String payeeProcessor) {
        this.payeeProcessor = payeeProcessor;
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

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }
}
