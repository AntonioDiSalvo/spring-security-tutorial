package com.example.social.configuration;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;

@Configuration
@EnableWebSecurity(debug=true)
public class MySecurityConfig {

    // esclusioni per index.html, pagina di errore e librerie
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.cors(AbstractHttpConfigurer::disable).csrf(AbstractHttpConfigurer::disable);
        String[] EXCLUDED_URL = {
                "/",
                "index.html",
                "/error",
                "/webjars/**",
                "/social/user",
                "/logout"
        };

        http.authorizeHttpRequests(auth -> auth.requestMatchers(EXCLUDED_URL).permitAll());
        http.exceptionHandling(e -> e.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)));
        http.oauth2Login(Customizer.withDefaults());
        http.logout(l -> l.logoutSuccessUrl("/").permitAll());

        return http.build();
    }
}
