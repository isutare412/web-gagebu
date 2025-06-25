package me.redshore.web_gagebu.user.dto;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.lang.Nullable;
import me.redshore.web_gagebu.user.IdpType;
import me.redshore.web_gagebu.user.Role;

public record UserDto(UUID id, List<Role> roles, String nickname,
                      IdpType idpType, String idpIdentifier,
                      @Nullable String pictureUrl, @Nullable String email,
                      ZonedDateTime createdAt, ZonedDateTime updatedAt) {
}
