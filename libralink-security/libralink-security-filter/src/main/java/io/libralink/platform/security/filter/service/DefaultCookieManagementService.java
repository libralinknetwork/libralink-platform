package io.libralink.platform.security.filter.service;

import io.libralink.platform.security.filter.CookieManagementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

public class DefaultCookieManagementService implements CookieManagementService {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultCookieManagementService.class);

    @Value("libralink.local.development.disable.secure.cookie:false")
    private boolean isSecureCookieDisabled;

    @Override
    public Optional<String> getCookieValue(HttpServletRequest request, String cookieName) {
        LOG.warn("Default DefaultCookieManagementService#getCookieValue called");

        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return Optional.empty();
        }

        for (int i=0; i<cookies.length; i++) {
            Cookie cookie = cookies[i];
            if (cookieName.equals(cookie.getName())) {
                return Optional.ofNullable(cookie.getValue());
            }
        }
        return Optional.empty();
    }

    @Override
    public void removeCookie(HttpServletResponse response, String name, String path, String domain) {
        LOG.warn("Default DefaultCookieManagementService#removeCookie called");

        Cookie responseCookie = new Cookie(name, null);
        responseCookie.setPath(path);
        responseCookie.setDomain(domain);
        responseCookie.setValue("");
        responseCookie.setMaxAge(-1);
        responseCookie.setSecure(!isSecureCookieDisabled);
        response.addCookie(responseCookie);
    }

    /**
     * DEVELOPMENT PURPOSE ONLY
     */
    @Override
    public void addCookieToResponse(HttpServletResponse response, String name, String path, String domain, String value) {
        LOG.warn("Default DefaultCookieManagementService#addCookieToResponse called");

        Cookie responseCookie = new Cookie(name, value);
        responseCookie.setPath(path);
        responseCookie.setDomain(domain);
        responseCookie.setSecure(!isSecureCookieDisabled);
        response.addCookie(responseCookie);
    }
}
