package me.redshore.web_gagebu.feature.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import me.redshore.web_gagebu.feature.user.validation.NicknameValidation;

public record UserUpdateRequest(
    @Schema(example = "Bob")
    @NicknameValidation
    String nickname
) {

}
