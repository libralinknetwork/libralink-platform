package io.libralink.platform.security.service.config;

import io.libralink.platform.security.service.service.DefaultTokenService;
import io.libralink.platform.security.service.service.DefaultUserProvisioningService;
import io.libralink.platform.security.service.TokenService;
import io.libralink.platform.security.service.UserProvisioningService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("io.libralink.platform.security")
public class SecurityServiceConfig {

    @Value(value = "${libralink.token.jwk}")
    private String jwk;

    @Value(value = "${libralink.token.expiration.hours}")
    private int tokenExpirationHours;

    @Bean
    @ConditionalOnMissingBean(UserProvisioningService.class)
    public UserProvisioningService userProvisioningService() {
        return new DefaultUserProvisioningService();
    }

    @Bean
    @ConditionalOnMissingBean(TokenService.class)
    public TokenService tokenService() {
        return new DefaultTokenService(jwk, tokenExpirationHours);
    }
}
