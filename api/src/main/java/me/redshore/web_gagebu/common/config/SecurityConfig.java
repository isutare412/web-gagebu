package me.redshore.web_gagebu.common.config;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import me.redshore.web_gagebu.feature.auth.handler.CustomAccessDeniedHandler;
import me.redshore.web_gagebu.feature.auth.handler.CustomAuthenticationEntryPoint;
import me.redshore.web_gagebu.feature.auth.jwt.JwtAutoRenewalFilter;
import me.redshore.web_gagebu.feature.auth.jwt.JwtConverter;
import me.redshore.web_gagebu.feature.auth.jwt.JwtCookieSetter;
import me.redshore.web_gagebu.feature.auth.jwt.JwtProvider;
import me.redshore.web_gagebu.feature.auth.oidc.CustomOidcUserService;
import me.redshore.web_gagebu.feature.auth.oidc.OidcRequestRepository;
import me.redshore.web_gagebu.feature.auth.oidc.OidcRequestResolver;
import me.redshore.web_gagebu.feature.auth.oidc.OidcSuccessHandler;
import me.redshore.web_gagebu.feature.auth.resolver.CustomBearerTokenResolver;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.CookieClearingLogoutHandler;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(offset = 110)
// Ensure transaction management is applied before security
@EnableTransactionManagement(order = 100)
@RequiredArgsConstructor
public class SecurityConfig {

    public static final String AUTHZ_BASE_URI = "/api/v1/oauth2/authorization";
    public static final String CODE_BASE_URI = "/api/v1/oauth2/code";
    public static final String LOGOUT_URI = "/api/v1/logout";

    private final OidcRequestResolver oidcRequestResolver;

    private final OidcSuccessHandler successHandler;

    private final OidcRequestRepository oidcRequestRepository;

    private final CustomOidcUserService oidcUserService;

    private final CustomBearerTokenResolver bearerTokenResolver;

    private final CustomAuthenticationEntryPoint authenticationEntryPoint;

    private final CustomAccessDeniedHandler accessDeniedHandler;

    private final JwtProvider jwtProvider;

    private final JwtConverter jwtConverter;

    private final JwtAutoRenewalFilter jwtAutoRenewalFilter;

    private final CorsProperties corsProperties;

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .formLogin(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http
            .oauth2Login(oauth2 -> oauth2
                .authorizationEndpoint(endpoint -> endpoint
                    .baseUri(AUTHZ_BASE_URI)
                    .authorizationRequestRepository(this.oidcRequestRepository)
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
                .addLogoutHandler(
                    new CookieClearingLogoutHandler(JwtCookieSetter.TOKEN_COOKIE_NAME))
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
                .requestMatchers("/api/*/users/me").permitAll()
                .requestMatchers("/api/**").authenticated()
                .anyRequest().permitAll());

        http.addFilterAfter(this.jwtAutoRenewalFilter, BearerTokenAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    FilterRegistrationBean<JwtAutoRenewalFilter> disableJwtAutoRenewalFilterRegistration(
        JwtAutoRenewalFilter filter) {
        var regitrationBean = new FilterRegistrationBean<>(filter);
        regitrationBean.setEnabled(false);
        return regitrationBean;
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        final var config = new CorsConfiguration();

        config.setAllowCredentials(this.corsProperties.isAllowCredentials());
        this.corsProperties.getAllowedOrigins().forEach(config::addAllowedOriginPattern);
        this.corsProperties.getAllowedMethods().forEach(config::addAllowedMethod);
        this.corsProperties.getAllowedHeaders().forEach(config::addAllowedHeader);

        final var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration(corsProperties.getPathPattern(), config);

        return source;
    }

}
