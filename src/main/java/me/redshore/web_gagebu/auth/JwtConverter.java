package me.redshore.web_gagebu.auth;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.stereotype.Component;
import me.redshore.web_gagebu.user.dto.UserJwtPayload;

@Component
public class JwtConverter extends JwtAuthenticationConverter {

    public JwtConverter() {
        super();
        setJwtGrantedAuthoritiesConverter(JwtConverter::getAuthoritiesFromJwt);
    }

    private static Collection<GrantedAuthority> getAuthoritiesFromJwt(Jwt jwt) {
        var roles = jwt.getClaimAsStringList(UserJwtPayload.ROLES_CLAIM_KEY);
        if (roles == null) {
            return List.of();
        }

        return roles.stream()
            .filter(role -> role != null)
            .map(role -> new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
            .collect(Collectors.toList());
    }

}
