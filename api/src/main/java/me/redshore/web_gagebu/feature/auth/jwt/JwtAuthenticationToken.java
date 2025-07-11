package me.redshore.web_gagebu.feature.auth.jwt;

import java.io.Serial;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.Getter;
import org.springframework.lang.Nullable;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

@Getter
public class JwtAuthenticationToken extends AbstractAuthenticationToken {

    @Serial
    private static final long serialVersionUID = 74326199821L;

    private final JwtUserPayload payload;

    @Nullable
    private final Instant expiresAt;

    public JwtAuthenticationToken(Jwt jwt) {
        super(extractAuthorities(jwt));

        this.payload = new JwtUserPayload(jwt);
        this.expiresAt = jwt.getExpiresAt();
        setAuthenticated(true);
    }

    @Override
    public String getName() {
        return this.payload.getId().toString();
    }

    @Override
    public Object getPrincipal() {
        return this.payload;
    }

    @Override
    public Object getCredentials() {
        return this.payload;
    }

    private static Collection<GrantedAuthority> extractAuthorities(Jwt jwt) {
        var roles = jwt.getClaimAsStringList(JwtUserPayload.ROLES_CLAIM_KEY);
        if (roles == null) {
            return List.of();
        }

        return roles.stream()
                    .filter(Objects::nonNull)
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
                    .collect(Collectors.toList());
    }

}
