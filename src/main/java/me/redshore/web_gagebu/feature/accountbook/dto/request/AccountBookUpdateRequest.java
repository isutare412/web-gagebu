package me.redshore.web_gagebu.feature.accountbook.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import me.redshore.web_gagebu.feature.user.validation.AccountBookNameValidation;

public record AccountBookUpdateRequest(
    @Schema(example = "My Second Account Book") @AccountBookNameValidation String name
) {

}
