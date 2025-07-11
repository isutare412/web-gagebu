package me.redshore.web_gagebu.feature.auth.oidc;

import java.io.Serial;
import me.redshore.web_gagebu.feature.auth.jwt.JwtUserPayload;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

public class CustomOidcUser extends DefaultOidcUser {

    @Serial
    private static final long serialVersionUID = 928357192389L;

    private final JwtUserPayload userPayload;

    public CustomOidcUser(OidcUser oidcUser, JwtUserPayload userPayload) {
        super(oidcUser.getAuthorities(), oidcUser.getIdToken(), oidcUser.getUserInfo());
        this.userPayload = userPayload;
    }

    public JwtUserPayload getUserJwtPayload() {
        return this.userPayload;
    }

}
