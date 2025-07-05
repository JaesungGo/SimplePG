package me.jaesung.simplepg.config;

import lombok.extern.slf4j.Slf4j;
import me.jaesung.simplepg.common.filter.ApiAuthenticationFilter;
import me.jaesung.simplepg.service.api.ApiCredentialService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Slf4j
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final ApiCredentialService apiCredentialService;

    public SecurityConfig(ApiCredentialService apiCredentialService) {
        this.apiCredentialService = apiCredentialService;
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests(auth -> auth
                        .requestMatchers("/test/**").permitAll()
                        .requestMatchers("/api/**").hasRole("")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(authenticationFilter(), UsernamePasswordAuthenticationFilter.class);
    }

    @Override
    public void configure(WebSecurity web) {
        web.ignoring().antMatchers("/resources/**");
    }

    @Bean
    public ApiAuthenticationFilter authenticationFilter() {
        return new ApiAuthenticationFilter(apiCredentialService);
    }


}