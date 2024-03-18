package com.example.social.configuration;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;

@Configuration
@EnableWebSecurity(debug=true)
public class MySecurityConfig {

    // esclusioni per index.html, pagina di errore e librerie
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        String[] EXCLUDED_URL = {
                "/",
                "index.html",
                "/error",
                "/webjars/**",
                "/social/user"
        };

        http.authorizeHttpRequests(auth -> auth.requestMatchers(EXCLUDED_URL).permitAll());
        http.exceptionHandling(e -> e.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)));
        http.oauth2Login(Customizer.withDefaults());
        return http.build();
    }
}
