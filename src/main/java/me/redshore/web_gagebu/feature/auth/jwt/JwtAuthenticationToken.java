package me.redshore.web_gagebu.feature.auth.jwt;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

public class JwtAuthenticationToken extends AbstractAuthenticationToken {

    private static final long serialVersionUID = 74326199821L;

    private final JwtUserPayload payload;

    public JwtAuthenticationToken(Jwt jwt) {
        super(extractAuthorities(jwt));
        this.payload = new JwtUserPayload(jwt);

        setAuthenticated(true);
    }

    public JwtUserPayload getPayload() {
        return this.payload;
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
            .filter(role -> role != null)
            .map(role -> new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
            .collect(Collectors.toList());
    }

}
