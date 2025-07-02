package me.redshore.web_gagebu.feature.accountbook.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.ZonedDateTime;
import java.util.UUID;
import me.redshore.web_gagebu.feature.accountbook.domain.MemberRole;

public record MemberView(
    UUID id,
    UUID userId,
    MemberRole role,

    @Schema(example = "Marilly")
    String nickname,

    @Schema(example = "https://example.com/pic.png")
    String pictureUrl,

    ZonedDateTime createdAt,
    ZonedDateTime updatedAt
) {

}
