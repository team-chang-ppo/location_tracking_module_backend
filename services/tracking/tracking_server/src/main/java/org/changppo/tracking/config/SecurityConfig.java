package org.changppo.tracking.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.changppo.tracking.jwt.JwtAccessDeniedHandler;
import org.changppo.tracking.jwt.JwtAuthenticationEntryPoint;
import org.changppo.tracking.jwt.TokenProvider;
import org.changppo.tracking.jwt.filter.JwtAuthenticationFilter;
import org.changppo.utils.jwt.tracking.TrackingJwtHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.RequestCacheConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.ExceptionTranslationFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@RequiredArgsConstructor
@Configuration
public class SecurityConfig {

    private final AccessDeniedHandler accessDeniedHandler;

    @Bean
    public SecurityFilterChain httpSecurity(HttpSecurity http, AuthenticationManager authenticationManager, TrackingJwtHandler trackingJwtHandler) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .sessionManagement((s) -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .rememberMe(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .requestCache(RequestCacheConfigurer::disable)

                .authorizeHttpRequests((authorizeRequests) -> authorizeRequests
                        .requestMatchers("/api/tracking/*/generate-token").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/tracking/*/start").hasAuthority("WRITE_TRACKING_COORDINATE")
                        .requestMatchers(HttpMethod.POST, "/api/tracking/*/tracking").hasAuthority("WRITE_TRACKING_COORDINATE")
                        .requestMatchers(HttpMethod.GET, "api/tracking/*/end").hasAuthority("WRITE_TRACKING_COORDINATE")
                        .requestMatchers(HttpMethod.GET, "/api/tracking/*/tracking").hasAuthority("READ_TRACKING_COORDINATE")
                        .anyRequest().authenticated())

                .addFilterAfter(new JwtAuthenticationFilter(authenticationManager, trackingJwtHandler), UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(e -> e
                        .authenticationEntryPoint(new JwtAuthenticationEntryPoint())
                        .accessDeniedHandler(accessDeniedHandler));

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            TokenProvider tokenProvider
    ){
        ProviderManager providerManager = new ProviderManager(tokenProvider);
        providerManager.setEraseCredentialsAfterAuthentication(true);
        return providerManager;
    }
}