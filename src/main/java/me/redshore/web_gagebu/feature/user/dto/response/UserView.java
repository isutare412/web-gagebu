package me.redshore.web_gagebu.feature.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import me.redshore.web_gagebu.feature.user.domain.IdpType;
import me.redshore.web_gagebu.feature.user.domain.UserRole;
import org.springframework.lang.Nullable;

public record UserView(
    UUID id,
    List<UserRole> roles,

    @Schema(example = "Bob")
    String nickname,

    IdpType idpType,

    @Schema(example = "19817852398")
    String idpIdentifier,

    @Schema(example = "https://example.com/amazing-thumbnail")
    @Nullable
    String pictureUrl,

    @Schema(example = "user@example.com")
    @Nullable
    String email,

    ZonedDateTime createdAt,
    ZonedDateTime updatedAt
) {

}
