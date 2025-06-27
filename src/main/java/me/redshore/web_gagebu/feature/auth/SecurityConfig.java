package me.redshore.web_gagebu.feature.auth;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import me.redshore.web_gagebu.feature.auth.handler.CustomAccessDeniedHandler;
import me.redshore.web_gagebu.feature.auth.handler.CustomAuthenticationEntryPoint;
import me.redshore.web_gagebu.feature.auth.jwt.JwtConverter;
import me.redshore.web_gagebu.feature.auth.jwt.JwtProvider;
import me.redshore.web_gagebu.feature.auth.oidc.CustomOidcUserService;
import me.redshore.web_gagebu.feature.auth.oidc.OidcRequestResolver;
import me.redshore.web_gagebu.feature.auth.oidc.OidcSuccessHandler;
import me.redshore.web_gagebu.feature.auth.resolver.CustomBearerTokenResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.CookieClearingLogoutHandler;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    public static final String AUTHZ_BASE_URI = "/api/v1/oauth2/authorization";
    public static final String CODE_BASE_URI = "/api/v1/oauth2/code";
    public static final String LOGOUT_URI = "/api/v1/logout";

    private final OidcRequestResolver oidcRequestResolver;

    private final OidcSuccessHandler successHandler;

    private final CustomOidcUserService oidcUserService;

    private final CustomBearerTokenResolver bearerTokenResolver;

    private final CustomAuthenticationEntryPoint authenticationEntryPoint;

    private final CustomAccessDeniedHandler accessDeniedHandler;

    private final JwtProvider jwtProvider;

    private final JwtConverter jwtConverter;

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .formLogin(form -> form.disable())
            .httpBasic(basic -> basic.disable())
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED));

        http
            .oauth2Login(oauth2 -> oauth2
                .authorizationEndpoint(endpoint -> endpoint
                    .baseUri(AUTHZ_BASE_URI)
                    .authorizationRequestResolver(this.oidcRequestResolver))
                .redirectionEndpoint(endpoint -> endpoint
                    .baseUri(CODE_BASE_URI + "/*"))
                .userInfoEndpoint(endpoint -> endpoint
                    .oidcUserService(this.oidcUserService))
                .successHandler(this.successHandler))
            .oauth2ResourceServer(server -> server
                .jwt(jwt -> jwt
                    .decoder(this.jwtProvider)
                    .jwtAuthenticationConverter(this.jwtConverter))
                .authenticationEntryPoint(this.authenticationEntryPoint)
                .bearerTokenResolver(this.bearerTokenResolver))
            .logout(logout -> logout
                .logoutUrl(LOGOUT_URI)
                .addLogoutHandler(new CookieClearingLogoutHandler(
                    OidcSuccessHandler.TOKEN_COOKIE_NAME))
                .logoutSuccessHandler((request, response, authentication) -> response
                    .setStatus(HttpServletResponse.SC_OK)))
            .exceptionHandling(exception -> exception
                .authenticationEntryPoint(this.authenticationEntryPoint)
                .accessDeniedHandler(this.accessDeniedHandler));

        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/*/oauth2/**").permitAll()
                .requestMatchers("/api/*/logout").permitAll()
                .requestMatchers("/api/*/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/**").authenticated()
                .anyRequest().permitAll());

        return http.build();
    }

}
