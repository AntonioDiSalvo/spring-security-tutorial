package com.example.social.configuration;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;

import java.util.List;
import java.util.Map;

import static org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction.oauth2AuthorizedClient;

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
        http.oauth2Login(o ->
                o.failureHandler((req, res, ex) -> {
                    req.getSession().setAttribute("error.message", ex.getMessage());
                }));

        http.logout(l -> l.logoutSuccessUrl("/").permitAll());

        return http.build();
    }

    @Bean
    public WebClient rest(ClientRegistrationRepository clients, OAuth2AuthorizedClientRepository auth) {

        ServletOAuth2AuthorizedClientExchangeFilterFunction oauth2Filter =
                new ServletOAuth2AuthorizedClientExchangeFilterFunction(clients, auth);

        return WebClient.builder().
                filter(oauth2Filter).build();
    }

    @Bean
    public OAuth2UserService<OAuth2UserRequest, OAuth2User> oauth2UserService(WebClient rest) {
        DefaultOAuth2UserService oauth2UserService = new DefaultOAuth2UserService();

        return param -> {
            OAuth2User user = oauth2UserService.loadUser(param);
            if (!"github".equals(param.getClientRegistration().getRegistrationId())) {
                return user;
            }

            OAuth2AuthorizedClient client = new OAuth2AuthorizedClient(
                    param.getClientRegistration(), user.getName(), param.getAccessToken());

            // attributo con URL da richiamare per ulteriore controllo
            String url = user.getAttribute("organizations_url");

            // controllo se l'utente appartiene ad una certa organizzazione
            final String orgToCheck = "demo-org-spring";

            // chiamata verso i servizi github, autenticati con il token reperito dal flusso auth
            // con la configurazione
            List<Map<String,Object>> orgs = rest
                    .get().uri(url)
                    .attributes(oauth2AuthorizedClient(client))
                    .retrieve()
                    .bodyToMono(List.class)
                    .block();

            if (orgs.stream().anyMatch(org -> orgToCheck.equals(org.get("login")))) {
                return user;
            }

            throw new OAuth2AuthenticationException(
                    (new OAuth2Error("invalid_token", "utente non appartenente alla company " + orgToCheck, "")));

        };
    }
}
