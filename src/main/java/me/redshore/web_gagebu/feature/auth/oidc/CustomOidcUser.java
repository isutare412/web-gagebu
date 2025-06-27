package me.redshore.web_gagebu.feature.auth.oidc;

import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import me.redshore.web_gagebu.feature.auth.jwt.JwtUserPayload;

public class CustomOidcUser extends DefaultOidcUser {

    private static final long serialVersionUID = 928357192389L;

    private JwtUserPayload userPayload;

    public CustomOidcUser(OidcUser oidcUser, JwtUserPayload userPayload) {
        super(oidcUser.getAuthorities(), oidcUser.getIdToken(), oidcUser.getUserInfo());
        this.userPayload = userPayload;
    }

    public JwtUserPayload getUserJwtPayload() {
        return this.userPayload;
    }

}
