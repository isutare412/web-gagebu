package me.redshore.web_gagebu.feature.user.dto;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import me.redshore.web_gagebu.feature.user.domain.IdpType;
import me.redshore.web_gagebu.feature.user.domain.UserRole;
import org.springframework.lang.Nullable;

public record UserDto(
    UUID id,
    List<UserRole> roles,
    String nickname,
    IdpType idpType,
    String idpIdentifier,
    @Nullable String pictureUrl,
    @Nullable String email,
    ZonedDateTime createdAt,
    ZonedDateTime updatedAt
) {

}
