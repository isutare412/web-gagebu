package me.redshore.web_gagebu.feature.invitation.dto.response;

import java.time.ZonedDateTime;
import java.util.UUID;
import org.springframework.lang.Nullable;

public record InvitationView(
    UUID id,
    @Nullable ZonedDateTime expiration,
    ZonedDateTime createdAt,
    ZonedDateTime updatedAt
) {

}
