package io.libralink.platform.security.filter.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.impl.TextCodec;
import io.libralink.platform.security.filter.TokenParsingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

public class DefaultTokenParsingService implements TokenParsingService {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultTokenParsingService.class);

    @Value(value = "${libralink.token.jwk}")
    private String jwk;

    @Override
    public Jws<Claims> parse(String jwtToken) throws JwtException {
        return Jwts.parser()
                .setSigningKey(TextCodec.BASE64.decode(jwk))
                .parseClaimsJws(jwtToken);
    }
}
