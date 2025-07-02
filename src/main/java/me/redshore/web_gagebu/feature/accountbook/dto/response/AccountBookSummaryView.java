package me.redshore.web_gagebu.feature.accountbook.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.ZonedDateTime;
import java.util.UUID;

public record AccountBookSummaryView(
    UUID id,

    @Schema(example = "My Account Book")
    String name,

    ZonedDateTime createdAt,
    ZonedDateTime updatedAt
) {

}
