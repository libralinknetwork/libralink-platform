package io.libralink.platform.security.filter.config;

import io.libralink.platform.security.filter.AuthorizationFilter;
import io.libralink.platform.security.filter.CookieManagementService;
import io.libralink.platform.security.filter.TokenParsingService;
import io.libralink.platform.security.filter.service.DefaultAuthorizationFilter;
import io.libralink.platform.security.filter.service.DefaultCookieManagementService;
import io.libralink.platform.security.filter.service.DefaultTokenParsingService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("io.libralink.platform.security.filter")
public class SecurityFilterConfig {

    @Bean
    @ConditionalOnMissingBean(TokenParsingService.class)
    public TokenParsingService tokenParsingService() {
        return new DefaultTokenParsingService();
    }

    @Bean
    @ConditionalOnMissingBean(CookieManagementService.class)
    public CookieManagementService cookieManagementService() {
        return new DefaultCookieManagementService();
    }

    @Bean
    @ConditionalOnMissingBean(AuthorizationFilter.class)
    public AuthorizationFilter authorizationFilter(CookieManagementService cookieManagementService, TokenParsingService tokenParsingService) {
        return new DefaultAuthorizationFilter(cookieManagementService, tokenParsingService);
    }
}
