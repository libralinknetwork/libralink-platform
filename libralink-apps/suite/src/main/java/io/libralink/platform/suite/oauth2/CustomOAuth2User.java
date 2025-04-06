package io.libralink.platform.suite.oauth2;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

public class CustomOAuth2User implements OAuth2User {

    private OAuth2User oauth2User;
    private String providerToken;

    public CustomOAuth2User(OAuth2User oauth2User, String providerToken) {
        this.oauth2User = oauth2User;
        this.providerToken = providerToken;
    }

    public String getProviderToken() {
        return providerToken;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return oauth2User.getAttributes();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return oauth2User.getAuthorities();
    }

    @Override
    public String getName() {
        String name = (String) oauth2User.getAttributes().get("name");
        return name != null ? name: "No Name";
    }
}