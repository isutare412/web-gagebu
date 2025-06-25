package me.redshore.web_gagebu.auth;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
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
        var role = jwt.getClaimAsString(UserJwtPayload.ROLE_CLAIM_KEY);
        if (role == null) {
            return Collections.emptyList();
        }

        return List.of(new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()));
    }

}
