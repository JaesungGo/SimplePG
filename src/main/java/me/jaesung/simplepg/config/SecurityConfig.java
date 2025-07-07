package me.jaesung.simplepg.config;

import lombok.extern.slf4j.Slf4j;
import me.jaesung.simplepg.common.filter.ApiAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;

@Slf4j
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final ApiAuthenticationFilter authenticationFilter;

    public SecurityConfig(ApiAuthenticationFilter authenticationFilter) {
        this.authenticationFilter = authenticationFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/test/**").permitAll()
                        .requestMatchers("/resources/**", "/assets/**", "/static/**").permitAll()
                        .requestMatchers("/api/protected/**").hasRole("API_CLIENT")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(authenticationFilter, AuthorizationFilter.class);

        return http.build();
    }

}