package io.libralink.platform.security.service.service;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.TextCodec;
import io.libralink.platform.security.common.constants.SecurityConstants;
import io.libralink.platform.security.service.TokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.UUID;

public class DefaultTokenService implements TokenService {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultTokenService.class);

    private String jwk;
    private Integer tokenExpirationHours;

    public DefaultTokenService(String jwk, Integer tokenExpirationHours) {
        this.jwk = jwk;
        this.tokenExpirationHours = tokenExpirationHours;
    }

    public String issueToken(UUID userId, String role) {
        LOG.warn("Default DefaultTokenService#issueToken called");

        LocalDateTime now = LocalDateTime.now();
        JwtBuilder builder = Jwts.builder()
                .setIssuer("Libralink")
                .setSubject(userId.toString())
                .claim(SecurityConstants.TOKEN_CLAIM_ROLE, role)
                .setIssuedAt(new Date(now.toInstant(ZoneOffset.UTC).toEpochMilli()))
                .signWith(SignatureAlgorithm.HS256, TextCodec.BASE64.decode(jwk));

        builder.setExpiration(new Date(now.plusHours(tokenExpirationHours).toInstant(ZoneOffset.UTC).toEpochMilli()));
        return builder.compact();
    }
}
