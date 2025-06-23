package me.redshore.web_gagebu.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import lombok.RequiredArgsConstructor;
import me.redshore.web_gagebu.auth.CustomBearerTokenResolver;
import me.redshore.web_gagebu.auth.CustomOidcUserService;
import me.redshore.web_gagebu.auth.JwtProvider;
import me.redshore.web_gagebu.auth.OidcRequestResolver;
import me.redshore.web_gagebu.auth.OidcSuccessHandler;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    public static final String AUTHZ_BASE_URI = "/api/v1/oauth2/authorization";
    public static final String CODE_BASE_URI = "/api/v1/oauth2/code";

    private final OidcRequestResolver oidcRequestResolver;

    private final OidcSuccessHandler successHandler;

    private final CustomOidcUserService oidcUserService;

    private final CustomBearerTokenResolver bearerTokenResolver;

    private final JwtProvider jwtProvider;

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .formLogin(form -> form.disable())
            .httpBasic(basic -> basic.disable())

            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))

            .oauth2Login(oauth2 -> oauth2
                .authorizationEndpoint(endpoint -> endpoint
                    .baseUri(AUTHZ_BASE_URI)
                    .authorizationRequestResolver(oidcRequestResolver))
                .redirectionEndpoint(endpoint -> endpoint
                    .baseUri(CODE_BASE_URI + "/*"))
                .userInfoEndpoint(endpoint -> endpoint
                    .oidcUserService(oidcUserService))
                .successHandler(successHandler))
            .oauth2ResourceServer(server -> server
                .jwt(jwt -> jwt
                    .decoder(jwtProvider))
                .bearerTokenResolver(bearerTokenResolver));

        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/v1/oauth2/**").permitAll()
                .requestMatchers("/api/**").authenticated()
                .anyRequest().permitAll());

        return http.build();
    }

}
