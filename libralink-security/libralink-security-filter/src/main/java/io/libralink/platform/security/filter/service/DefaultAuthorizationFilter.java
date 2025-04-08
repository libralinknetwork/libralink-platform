package io.libralink.platform.security.filter.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.libralink.platform.security.common.constants.SecurityConstants;
import io.libralink.platform.security.common.principal.LibralinkUser;
import io.libralink.platform.security.filter.AuthorizationFilter;
import io.libralink.platform.security.filter.CookieManagementService;
import io.libralink.platform.security.filter.TokenParsingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DefaultAuthorizationFilter extends OncePerRequestFilter implements AuthorizationFilter {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultAuthorizationFilter.class);

    private CookieManagementService cookieManagementService;
    private TokenParsingService tokenParsingService;

    public DefaultAuthorizationFilter(CookieManagementService cookieManagementService, TokenParsingService tokenParsingService) {
        this.cookieManagementService = cookieManagementService;
        this.tokenParsingService = tokenParsingService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        authorize(request, response);
        filterChain.doFilter(request, response);
    }

    private void authorize(HttpServletRequest request, HttpServletResponse response) {
        /* Try to get Token from Cookies */
        String jwtToken = null;

        Optional<String> jwtTokenOption = cookieManagementService.getCookieValue(request, SecurityConstants.COOKIE_NAME_TOKEN);
        if (jwtTokenOption.isEmpty()) {
            /* Try to get Token from Authorization header */
            String authorizationHeader = request.getHeader("Authorization");
            jwtToken = authorizationHeader != null ? authorizationHeader.split(" ")[1]: null;
        } else {
            jwtToken = jwtTokenOption.get();
        }

        if (jwtToken == null || jwtToken.length() == 0) {
            return;
        }

        try {
            Jws<Claims> token = tokenParsingService.parse(jwtToken);

            final String role = token.getBody().get(SecurityConstants.TOKEN_CLAIM_ROLE, String.class);
            List<SimpleGrantedAuthority> authorities = role != null ? List.of(new SimpleGrantedAuthority(role)): new ArrayList<>();

            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                    new LibralinkUser(
                            token.getBody().get(SecurityConstants.TOKEN_CLAIM_SUBJECT, String.class),
                            jwtToken,
                            authorities
                    ),
                    null, authorities
            );
            SecurityContextHolder.getContext().setAuthentication(auth);

        } catch (JwtException exception) {
            LOG.error(exception.getMessage());

            cookieManagementService.removeCookie(response, SecurityConstants.COOKIE_NAME_TOKEN,
                    SecurityConstants.COOKIE_PATH, SecurityConstants.COOKIE_DOMAIN);
        }
    }
}
