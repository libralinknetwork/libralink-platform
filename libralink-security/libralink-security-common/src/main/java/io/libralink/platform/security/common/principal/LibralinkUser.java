package io.libralink.platform.security.common.principal;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.UUID;

public final class LibralinkUser extends User {

    private String token;

    public LibralinkUser(String userId, String token, Collection<? extends GrantedAuthority> authorities) {
        super(userId, UUID.randomUUID().toString(), true, true, true, true, authorities);
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
