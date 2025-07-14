package me.redshore.web_gagebu.feature.accountbook.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;
import me.redshore.web_gagebu.feature.accountbook.validation.CategoryNameValidation;
import org.springframework.lang.Nullable;

public record CategoryUpdateRequest(
    @Schema(description = "Category ID - null for new categories")
    @Nullable
    UUID id,

    @Schema(description = "Category name", example = "Food & Dining")
    @CategoryNameValidation
    String name
) {

}
