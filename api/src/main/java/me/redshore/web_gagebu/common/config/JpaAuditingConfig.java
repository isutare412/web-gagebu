package me.redshore.web_gagebu.common.config;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;
import me.redshore.web_gagebu.feature.auth.jwt.JwtAuthenticationToken;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Configuration
@EnableJpaAuditing(dateTimeProviderRef = "zonedDateTimeProvider")
public class JpaAuditingConfig {

    @Bean
    DateTimeProvider zonedDateTimeProvider() {
        return () -> Optional.of(ZonedDateTime.now(ZoneOffset.UTC));
    }

    @Bean
    AuditorAware<UUID> jwtAuditorProvider() {
        return () -> Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                             .filter(Authentication::isAuthenticated)
                             .filter(JwtAuthenticationToken.class::isInstance)
                             .map(JwtAuthenticationToken.class::cast)
                             .map(JwtAuthenticationToken::getName)
                             .flatMap(userId -> {
                                 try {
                                     return Optional.of(UUID.fromString(userId));
                                 } catch (IllegalArgumentException e) {
                                     return Optional.empty();
                                 }
                             });
    }

}
