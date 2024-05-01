package org.changppo.account.security;

import lombok.RequiredArgsConstructor;
import org.changppo.account.security.oauth2.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.client.JdbcOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.HttpSessionOAuth2AuthorizationRequestRepository;
import org.springframework.security.oauth2.client.web.HttpSessionOAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.DelegatingSecurityContextRepository;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final ClientRegistrationRepository clientRegistrationRepository;
    private final JdbcTemplate jdbcTemplate;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomLoginSuccessHandler customLoginSuccessHandler;
    private final CustomLoginFailureHandler customLoginFailureHandler;
    private final CustomLogoutSuccessHandler customLogoutSuccessHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)

                .securityContext((securityContext) -> {
                    securityContext.securityContextRepository(delegatingSecurityContextRepository());
                    securityContext.requireExplicitSave(true);
                })

                .exceptionHandling((exceptionConfig) ->
                        exceptionConfig.authenticationEntryPoint(customAuthenticationEntryPoint).accessDeniedHandler(customAccessDeniedHandler)
                )

                .addFilterBefore(
                        new PreOAuth2AuthorizationRequestFilter(clientRegistrationRepository, new HttpSessionOAuth2AuthorizationRequestRepository()),
                        OAuth2LoginAuthenticationFilter.class)

                .oauth2Login((oauth2) -> oauth2
                        .authorizedClientRepository(authorizedClientRepository())
                        .authorizedClientService(oAuth2AuthorizedClientService(jdbcTemplate, clientRegistrationRepository))
                        .userInfoEndpoint((userInfoEndpointConfig) -> userInfoEndpointConfig
                                            .userService(customOAuth2UserService))
                        .successHandler(customLoginSuccessHandler)
                        .failureHandler(customLoginFailureHandler))

                .logout(logout -> logout
                        .logoutUrl("/logout/oauth2")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID")
                        .logoutSuccessHandler(customLogoutSuccessHandler))

                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/oauth2/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/members/**").hasRole("FREE")
                        .requestMatchers(HttpMethod.DELETE, "/api/members/**").hasRole("FREE")
                        .requestMatchers(HttpMethod.POST, "/api/apikeys/v1/createFreeKey").hasRole("FREE")
                        .requestMatchers(HttpMethod.POST, "/api/apikeys/v1/createClassicKey").hasRole("NORMAL")
                        .requestMatchers(HttpMethod.GET, "/api/apikeys/**").hasRole("FREE")
                        .requestMatchers(HttpMethod.DELETE, "/api/apikeys/**").hasRole("FREE")
                        .requestMatchers(HttpMethod.POST, "/api/cards/v1/kakaopay/**").hasRole("FREE")
                        .requestMatchers(HttpMethod.GET, "/api/cards/v1/kakaopay/**").hasRole("FREE")
                        .requestMatchers(HttpMethod.GET, "/api/cards/**").hasRole("NORMAL")
                        .requestMatchers(HttpMethod.DELETE, "/api/cards/**").hasRole("NORMAL")
                        .requestMatchers(HttpMethod.POST, "/api/payments/**").hasRole("FREE")
                        .requestMatchers(HttpMethod.GET, "/api/payments/**").hasRole("FREE")
                        .requestMatchers(HttpMethod.GET).permitAll()
                        .anyRequest().hasRole("ADMIN"));

        return http.build();
    }

    @Bean
    public DelegatingSecurityContextRepository delegatingSecurityContextRepository() {
        return new DelegatingSecurityContextRepository(
                new RequestAttributeSecurityContextRepository(),
                new HttpSessionSecurityContextRepository()
        );
    }

    @Bean
    public OAuth2AuthorizedClientRepository authorizedClientRepository() {
        return new HttpSessionOAuth2AuthorizedClientRepository();
    }

    @Bean
    public OAuth2AuthorizedClientService oAuth2AuthorizedClientService(JdbcTemplate jdbcTemplate, ClientRegistrationRepository clientRegistrationRepository) {
        return new JdbcOAuth2AuthorizedClientService(jdbcTemplate ,clientRegistrationRepository);
    }

    @Bean
    public RoleHierarchy roleHierarchy() {
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        roleHierarchy.setHierarchy("ROLE_ADMIN > ROLE_NORMAL > ROLE_FREE");
        return roleHierarchy;
    }

    @Bean
    public MethodSecurityExpressionHandler methodSecurityExpressionHandler(RoleHierarchy roleHierarchy) {
        DefaultMethodSecurityExpressionHandler expressionHandler = new DefaultMethodSecurityExpressionHandler();
        expressionHandler.setRoleHierarchy(roleHierarchy);
        return expressionHandler;
    }
}