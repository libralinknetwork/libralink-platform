package io.libralink.platform.wallet.data.entity;

import io.libralink.platform.wallet.data.enums.DepositApprovalStatus;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "deposit_approval")
public class DepositApproval {

    @Id
    @Column(name = "approval_id")
    private String id;

    @Column(name = "account_id")
    private String accountId;

    @Column(name = "check_id")
    private String checkId;

    private BigDecimal amount;
    private String payer;
    private String payee;

    @Column(name = "created_at")
    private Long createdAt;

    @Enumerated(EnumType.STRING)
    private DepositApprovalStatus status;

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

    public String getCheckId() {
        return checkId;
    }

    public void setCheckId(String checkId) {
        this.checkId = checkId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getPayer() {
        return payer;
    }

    public void setPayer(String payer) {
        this.payer = payer;
    }

    public String getPayee() {
        return payee;
    }

    public void setPayee(String payee) {
        this.payee = payee;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public DepositApprovalStatus getStatus() {
        return status;
    }

    public void setStatus(DepositApprovalStatus status) {
        this.status = status;
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
