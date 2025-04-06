package io.libralink.platform.suite.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class TokenService {

    @Value(value = "${libralink.token.jwk}")
    private String jwk;

    @Value(value = "${libralink.token.expiration.hours}")
    private int tokenExpirationHours;

    public String issueToken(UUID userId, String role) {

//        return TokenUtils.issueToken("contrib-security", userId.toString(), role, jwk, tokenExpirationHours);
        return null;
    }
}
