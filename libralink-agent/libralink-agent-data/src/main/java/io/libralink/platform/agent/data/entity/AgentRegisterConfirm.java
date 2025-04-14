package io.libralink.platform.agent.data.entity;

import io.libralink.platform.agent.data.enums.ChallengeStatus;

import javax.persistence.*;

@Entity
@Table(name = "agent_register_confirm")
public class AgentRegisterConfirm {

    @Id
    @Column(name = "confirmation_id")
    private String id;

    @Column(name = "updated_at")
    private Long updatedAt;

    @Column(name = "expires_at")
    private Long expiresAt;

    @Enumerated(EnumType.STRING)
    private ChallengeStatus status;

    private String salt;

    @Column(name = "used_by_agent_id")
    private String usedByAgentId;

    @Column(name = "account_id")
    private String accountId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Long getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Long expiresAt) {
        this.expiresAt = expiresAt;
    }

    public ChallengeStatus getStatus() {
        return status;
    }

    public void setStatus(ChallengeStatus status) {
        this.status = status;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getUsedByAgentId() {
        return usedByAgentId;
    }

    public void setUsedByAgentId(String usedByAgentId) {
        this.usedByAgentId = usedByAgentId;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }
}
