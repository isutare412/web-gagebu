package me.redshore.web_gagebu.auth;

import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import me.redshore.web_gagebu.user.dto.UserJwtPayload;

public class CustomOidcUser extends DefaultOidcUser {

    private static final long serialVersionUID = 928357192389L;

    private UserJwtPayload userPayload;

    public CustomOidcUser(OidcUser oidcUser, UserJwtPayload userPayload) {
        super(oidcUser.getAuthorities(), oidcUser.getIdToken(), oidcUser.getUserInfo());
        this.userPayload = userPayload;
    }

    public UserJwtPayload getUserJwtPayload() {
        return this.userPayload;
    }

}
