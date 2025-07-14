package me.redshore.web_gagebu.feature.accountbook.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record CategoryListUpdateRequest(
    @Schema(description = "List of categories to update")
    @NotNull(message = "Categories list must not be null")
    @Valid
    List<CategoryUpdateRequest> categories
) {

}
