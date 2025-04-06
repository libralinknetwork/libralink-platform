package io.libralink.platform.suite.config;

import io.libralink.platform.suite.oauth2.CustomOAuth2UserService;
import io.libralink.platform.suite.service.TokenService;
import io.libralink.platform.wallet.services.AccountManagementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.oauth2.client.CommonOAuth2Provider;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.UUID;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private static final Logger LOG = LoggerFactory.getLogger(WebSecurityConfig.class);

    @Value("${libralink.auth.redirect.url}")
    private String libralinkRedirectUrl;

    @Value(value = "${libralink.token.jwk}")
    private String jwk;

//    @Autowired
//    private PaymentClient paymentClient;

    @Autowired
    private CustomOAuth2UserService oAuth2UserService;

    @Autowired
    private TokenService tokenService;

//    @Autowired
//    private AuthorizationFilter authorizationFilter;
//
//    @Autowired
//    private UserManagementService userManagementService;

    @Autowired
    private AccountManagementService walletManagementService;

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository(
        @Value(value = "${oauth2.github.clientId}") String clientId,
        @Value(value = "${oauth2.github.github.clientSecret}") String clientSecret
    ) {
        return new InMemoryClientRegistrationRepository(
            this.githubDefaultClientRegistration(clientId, clientSecret)
        );
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http
//            .addFilterAfter(authorizationFilter, UsernamePasswordAuthenticationFilter.class)
            .authorizeRequests(a -> a
                .antMatchers("/", "/error",
                    "/v2/api-docs",
                    "/v2/api-docs/**",
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/webjars/**",
                    "/swagger-resources/**",
                    "/api/logout",
                    "/account/**", "/echeck/**", "/processor/**"
                ).permitAll()
                .anyRequest().authenticated()
            )
            .exceptionHandling(e -> e
                .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
            )
            .csrf().disable();
    }

    private ClientRegistration githubDefaultClientRegistration(String clientId, String clientSecret) {
        return CommonOAuth2Provider.GITHUB.getBuilder("github")
            .clientId(clientId)
            .clientSecret(clientSecret)
            .scope("read:user", "user:email", "repo")
            .build();
    }
}