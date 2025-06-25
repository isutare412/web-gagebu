package me.redshore.web_gagebu.user.dto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.lang.Nullable;
import org.springframework.security.oauth2.jwt.Jwt;
import io.jsonwebtoken.Claims;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import me.redshore.web_gagebu.user.IdpType;
import me.redshore.web_gagebu.user.Role;

@EqualsAndHashCode
@AllArgsConstructor
@Builder
@Getter
public class UserJwtPayload {

    public static String ROLES_CLAIM_KEY = "roles";

    private final UUID id;

    private final List<Role> roles;

    private final String nickname;

    private final IdpType idpType;

    private final String idpIdentifier;

    private final @Nullable String pictureUrl;

    private final @Nullable String email;

    public UserJwtPayload(Jwt jwt) {
        this.id = UUID.fromString(jwt.getSubject());
        this.nickname = jwt.getClaimAsString("nickname");
        this.idpType = IdpType.valueOf(jwt.getClaimAsString("idpType"));
        this.idpIdentifier = jwt.getClaimAsString("idpIdentifier");
        this.pictureUrl = jwt.getClaimAsString("pictureUrl");
        this.email = jwt.getClaimAsString("email");

        List<String> rawRoles = jwt.getClaimAsStringList(ROLES_CLAIM_KEY);
        this.roles = parseRoles(rawRoles);
    }

    public UserJwtPayload(Claims claims) {
        this.id = UUID.fromString(claims.getSubject());
        this.nickname = claims.get("nickname", String.class);
        this.idpType = IdpType.valueOf(claims.get("idpType", String.class));
        this.idpIdentifier = claims.get("idpIdentifier", String.class);
        this.pictureUrl = claims.get("pictureUrl", String.class);
        this.email = claims.get("email", String.class);

        List<?> rawRoles = claims.get(ROLES_CLAIM_KEY, List.class);
        this.roles = parseRoles(rawRoles);
    }

    public Map<String, Object> toMap() {
        var map = new HashMap<String, Object>();
        map.put(ROLES_CLAIM_KEY, this.roles);
        map.put("nickname", this.nickname);
        map.put("idpType", this.idpType.name());
        map.put("idpIdentifier", this.idpIdentifier);
        map.put("pictureUrl", this.pictureUrl);
        map.put("email", this.email);
        return map;
    }

    private static List<Role> parseRoles(List<?> roles) {
        return roles.stream()
            .map(role -> Role.valueOf(role.toString()))
            .toList();
    }

}
