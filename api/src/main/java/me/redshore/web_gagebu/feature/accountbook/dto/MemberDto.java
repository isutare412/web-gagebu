package me.redshore.web_gagebu.feature.accountbook.dto;

import java.time.ZonedDateTime;
import java.util.UUID;
import me.redshore.web_gagebu.feature.accountbook.domain.MemberRole;

public record MemberDto(
    UUID id,
    UUID userId,
    MemberRole role,
    String nickname,
    String pictureUrl,
    ZonedDateTime createdAt,
    ZonedDateTime updatedAt
) {

}
