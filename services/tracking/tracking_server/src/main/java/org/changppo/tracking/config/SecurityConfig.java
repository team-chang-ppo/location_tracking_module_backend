package org.changppo.tracking.config;

import lombok.RequiredArgsConstructor;
import org.changppo.tracking.jwt.CustomAccessDeniedHandler;
import org.changppo.tracking.jwt.TokenProvider;
import org.changppo.tracking.jwt.filter.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.ExceptionTranslationFilter;

@EnableWebSecurity(debug = true)
@RequiredArgsConstructor
@Configuration
public class SecurityConfig {

    private final AuthenticationEntryPoint entryPoint;

    @Bean
    public SecurityFilterChain httpSecurity(HttpSecurity http, AuthenticationManager authenticationManager) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .sessionManagement((s) -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests((authorizeRequests) -> authorizeRequests
                        .requestMatchers("/api/tracking/*/generate-token").permitAll()
                        .requestMatchers("/api/**").authenticated())

                .addFilterAfter(new JwtAuthenticationFilter(authenticationManager), ExceptionTranslationFilter.class)
                .exceptionHandling(e -> e
                        .authenticationEntryPoint(entryPoint)
                        .accessDeniedHandler(new CustomAccessDeniedHandler()));

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