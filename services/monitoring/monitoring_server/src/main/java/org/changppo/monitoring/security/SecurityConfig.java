package org.changppo.monitoring.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.List;

@EnableMethodSecurity
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain httpSecurity(
            HttpSecurity security,
            RemoteSessionRetrieveStrategy remoteSessionRetrieveStrategy,
            ObjectMapper objectMapper,
            SessionQueryProperties sessionQueryProperties
    ) throws Exception {
        return security
                .sessionManagement(SessionManagementConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(HttpBasicConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .rememberMe(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .requestCache(AbstractHttpConfigurer::disable)
                .exceptionHandling(config -> {
                    config.accessDeniedHandler(new CustomAccessDeniedHandler());
//                    config.authenticationEntryPoint()
                })
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers("/api/aggregation/v1/ping").permitAll()
                                .requestMatchers("/error").permitAll()
                                .anyRequest().authenticated()
                )
                .addFilterAfter(new RemoteSessionAuthenticationFilter(remoteSessionRetrieveStrategy, sessionQueryProperties, objectMapper), UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    @Profile("local")
    public RemoteSessionRetrieveStrategy localRemoteSessionRetrieveStrategy() {
        return sessionId -> new RemoteSessionAuthentication(1L, List.of("ROLE_ADMIN"));
    }

    @Bean
    @Profile("prod")
    public RemoteSessionRetrieveStrategy prodRemoteSessionRetrieveStrategy(
            SessionQueryProperties sessionQueryProperties,
            RestTemplateBuilder restTemplateBuilder
    ) {
        return new RestRemoteSessionRetrieveStrategy(
                sessionQueryProperties,
                restTemplateBuilder.build()
        );
    }

}
