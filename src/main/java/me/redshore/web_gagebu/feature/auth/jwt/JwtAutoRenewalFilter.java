package me.redshore.web_gagebu.feature.auth.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.redshore.web_gagebu.common.config.properties.AuthProperties;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAutoRenewalFilter extends OncePerRequestFilter {

    private final AuthProperties authProperties;

    private final JwtCookieSetter jwtCookieSetter;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws
                                                                      ServletException,
                                                                      IOException {
        try {
            final var tokenOptional = getAuthenticationToken();
            if (tokenOptional.isEmpty()) {
                return;
            }

            final var token = tokenOptional.get();
            if (!isRenewalRequired(token)) {
                return;
            }

            log.info("Renew JWT token of user '{}'", token.getPayload().getId());
            this.jwtCookieSetter.setCookie(response, token.getPayload());
        } finally {
            filterChain.doFilter(request, response);
        }
    }

    private Optional<JwtAuthenticationToken> getAuthenticationToken() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                       .filter(Authentication::isAuthenticated)
                       .filter(JwtAuthenticationToken.class::isInstance)
                       .map(JwtAuthenticationToken.class::cast);
    }

    private boolean isRenewalRequired(JwtAuthenticationToken token) {
        return Optional.of(token)
                       .map(JwtAuthenticationToken::getExpiresAt)
                       .map(this::isAfterRenewalThreshold)
                       .orElse(false);
    }

    private boolean isAfterRenewalThreshold(@NonNull Instant expiry) {
        final var renewal = this.authProperties.getJwt().getRenewal();
        return Instant.now().plus(renewal).isAfter(expiry);
    }
}
