package me.redshore.web_gagebu.user.dto;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.springframework.lang.Nullable;
import io.jsonwebtoken.Claims;
import lombok.Builder;
import me.redshore.web_gagebu.user.IdpType;
import me.redshore.web_gagebu.user.Role;

@Builder
public record UserJwtPayload(UUID id, Role role, String nickname,
                             IdpType idpType, String idpIdentifier,
                             @Nullable String pictureUrl, @Nullable String email) {

    public static String ROLE_CLAIM_KEY = "role";

    public UserJwtPayload(Claims claims) {
        this(UUID.fromString(claims.getSubject()),
            claims.get(ROLE_CLAIM_KEY, Role.class),
            claims.get("nickname", String.class),
            IdpType.valueOf(claims.get("idpType", String.class)),
            claims.get("idpIdentifier", String.class),
            claims.get("pictureUrl", String.class),
            claims.get("email", String.class));
    }

    public Map<String, Object> toMap() {
        var map = new HashMap<String, Object>();
        map.put("nickname", this.nickname);
        map.put("idpType", this.idpType.name());
        map.put("idpIdentifier", this.idpIdentifier);
        map.put("pictureUrl", this.pictureUrl);
        map.put("email", this.email);
        map.put(ROLE_CLAIM_KEY, this.role);
        return map;
    }

}
