package org.changppo.monitoring.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.DispatcherType;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
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
            ObjectMapper objectMapper
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
                    config.authenticationEntryPoint(new CustomAuthenticationEntryPoint());
                })
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .anyRequest().permitAll() // use method security instead
                )
                .addFilterAfter(new RemoteSessionAuthenticationFilter(remoteSessionRetrieveStrategy, objectMapper), UsernamePasswordAuthenticationFilter.class)
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

    @Bean
    public FilterRegistrationBean<ErrorStateDebugFilter> errorStateDebugFilter() {
        FilterRegistrationBean<ErrorStateDebugFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new ErrorStateDebugFilter());
        registrationBean.addUrlPatterns("/*");
        registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        registrationBean.setDispatcherTypes(DispatcherType.REQUEST);
        return registrationBean;
    }

}
