package io.libralink.platform.agent.data.entity;

import javax.persistence.*;

@Entity
@Table(name = "agent")
public class Agent {

    @Id
    @Column(name = "agent_id")
    private String id;

    @Column(name = "account_id")
    private String accountId;

    @Column(name = "enabled")
    private boolean enabled;

    @Column(name = "created_at")
    private Long createdAt;

    @Column(name = "public_key")
    private String publicKey;
    private String address;
    private String algorithm;

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

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }
}
