package me.redshore.web_gagebu.feature.user.dto;

import java.util.UUID;

public record UserUpdateCommand(
    UUID userId,
    String nickname
) {

}
