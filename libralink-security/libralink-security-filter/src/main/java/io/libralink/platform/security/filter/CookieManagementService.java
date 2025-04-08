package io.libralink.platform.security.filter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

public interface CookieManagementService {

    Optional<String> getCookieValue(HttpServletRequest request, String cookieName);

    void removeCookie(HttpServletResponse response, String name, String path, String domain);

    void addCookieToResponse(HttpServletResponse response, String name, String path,
                             String domain, String value);
}
