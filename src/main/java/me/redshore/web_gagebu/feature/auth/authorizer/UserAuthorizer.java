package me.redshore.web_gagebu.feature.auth.authorizer;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import me.redshore.web_gagebu.feature.auth.jwt.JwtUserPayload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserAuthorizer {

    public boolean canAccessUser(UUID userId) {
        final var jwtUserPayload = JwtUserPayload.fromSecurityContext();
        return jwtUserPayload.getId().equals(userId);
    }

    public boolean canModifyUser(UUID userId) {
        final var jwtUserPayload = JwtUserPayload.fromSecurityContext();
        return jwtUserPayload.getId().equals(userId);
    }

}
