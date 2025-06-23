package me.redshore.web_gagebu.user.dto;

import java.time.ZonedDateTime;
import java.util.UUID;
import org.springframework.lang.Nullable;
import me.redshore.web_gagebu.user.IdpType;

public record UserDto(UUID id, String nickname, IdpType idpType, String idpIdentifier,
                      @Nullable String pictureUrl, @Nullable String email, ZonedDateTime createdAt,
                      ZonedDateTime updatedAt) {
}
