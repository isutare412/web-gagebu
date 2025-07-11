package me.redshore.web_gagebu.feature.invitation.dto;

import java.time.ZonedDateTime;
import java.util.UUID;
import org.springframework.lang.Nullable;

public record InvitationDto(
    UUID id,
    @Nullable ZonedDateTime expiration,
    ZonedDateTime createdAt,
    ZonedDateTime updatedAt
) {

}
