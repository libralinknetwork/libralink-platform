package io.libralink.platform.security.filter.service;

import io.libralink.platform.security.filter.CookieManagementService;
import org.springframework.beans.factory.annotation.Value;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

public class DefaultCookieManagementService implements CookieManagementService {

    @Value("${libralink.local.development.disable.secure.cookie:false}")
    private boolean isSecureCookieDisabled;

    @Override
    public Optional<String> getCookieValue(HttpServletRequest request, String cookieName) {
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
        Cookie responseCookie = new Cookie(name, null);
        responseCookie.setPath(path);
        responseCookie.setDomain(domain);
        responseCookie.setValue("");
        responseCookie.setMaxAge(-1);
        responseCookie.setSecure(!isSecureCookieDisabled);
        response.addCookie(responseCookie);
    }

    @Override
    public void addCookieToResponse(HttpServletResponse response, String name, String path, String domain, String value) {
        Cookie responseCookie = new Cookie(name, value);
        responseCookie.setPath(path);
        responseCookie.setDomain(domain);
        responseCookie.setSecure(!isSecureCookieDisabled);
        response.addCookie(responseCookie);
    }
}
