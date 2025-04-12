package io.libralink.platform.suite.config;

import io.libralink.platform.common.ApplicationException;
import io.libralink.platform.security.common.constants.SecurityConstants;
import io.libralink.platform.security.filter.*;
import io.libralink.platform.security.filter.service.DefaultAuthorizationFilter;
import io.libralink.platform.security.service.TokenService;
import io.libralink.platform.security.service.UserProvisioningService;
import io.libralink.platform.security.service.dto.UserDTO;
import io.libralink.platform.suite.oauth2.CustomOAuth2User;
import io.libralink.platform.suite.oauth2.CustomOAuth2UserService;
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

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private static final Logger LOG = LoggerFactory.getLogger(WebSecurityConfig.class);

    @Value("${libralink.local.development.disable.csrf:false}")
    private boolean springCsrfDisable;

    @Value("${libralink.auth.redirect.url}")
    private String libralinkRedirectUrl;

    @Value(value = "${libralink.token.jwk}")
    private String jwk;

    @Autowired
    private CustomOAuth2UserService oAuth2UserService;

    @Autowired
    private AuthorizationFilter authorizationFilter;

    @Autowired
    private UserProvisioningService userProvisioningService;

    @Autowired
    private CookieManagementService cookieManagementService;

    @Autowired
    private TokenService tokenService;

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
            .addFilterAfter(authorizationFilter, UsernamePasswordAuthenticationFilter.class)
            .authorizeRequests(a -> a
                .antMatchers("/", "/error",
                    "/v2/api-docs", "/v2/api-docs/**",
                    "/swagger-ui/**", "/swagger-ui.html", "/webjars/**", "/swagger-resources/**",
                    "/api/logout",
                    "/protocol/**"
                ).permitAll()
                .anyRequest().authenticated()
            )
            .exceptionHandling(e -> e
                .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
            )
            .oauth2Login()
                .userInfoEndpoint().userService(oAuth2UserService)
            .and()
                .successHandler((request, response, authentication) -> {
                    CustomOAuth2User oauthUser = (CustomOAuth2User) authentication.getPrincipal();

                    final String platform = "github";
                    final String platformUserId = oauthUser.getAttribute("id").toString();

                    try {
                        UserDTO user = userProvisioningService.provisionUser(platform, platformUserId);

                        String jwt = tokenService.issueToken(user.getUserId(), user.getRole());
                        cookieManagementService.addCookieToResponse(response, SecurityConstants.COOKIE_NAME_TOKEN,
                                SecurityConstants.COOKIE_PATH, SecurityConstants.COOKIE_DOMAIN, jwt);
                        response.sendRedirect(String.format("%s?action=login", libralinkRedirectUrl));

                    } catch (ApplicationException e) {
                        throw new RuntimeException(e);
                    }
                });

        if (springCsrfDisable) {
            http.csrf().disable();
        }
    }

    private ClientRegistration githubDefaultClientRegistration(String clientId, String clientSecret) {
        return CommonOAuth2Provider.GITHUB.getBuilder("github")
            .clientId(clientId)
            .clientSecret(clientSecret)
            .scope("read:user", "user:email")
            .build();
    }
}