package io.libralink.platform.security.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;

public interface TokenParsingService {

    Jws<Claims> parse(String token) throws JwtException;
}
