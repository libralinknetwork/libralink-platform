package io.libralink.platform.security.service.dto;

import java.util.UUID;

public class UserDTO {

    private UUID userId;
    private String role;

    public UserDTO() {
    }

    public UserDTO(UUID userId, String role) {
        this.userId = userId;
        this.role = role;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
