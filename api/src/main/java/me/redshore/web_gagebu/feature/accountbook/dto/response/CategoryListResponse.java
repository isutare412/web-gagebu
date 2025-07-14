package me.redshore.web_gagebu.feature.accountbook.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public record CategoryListResponse(
    @Schema(description = "List of categories")
    List<CategoryView> categories
) {

}
