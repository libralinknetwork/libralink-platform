package io.libralink.platform.security.service;

import java.util.UUID;

public interface TokenService {

    String issueToken(UUID userId, String role);
}
