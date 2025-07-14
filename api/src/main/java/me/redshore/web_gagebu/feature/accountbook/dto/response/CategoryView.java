package me.redshore.web_gagebu.feature.accountbook.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.ZonedDateTime;
import java.util.UUID;

public record CategoryView(
    UUID id,

    @Schema(example = "Hobby")
    String name,

    Boolean isBasic,
    Boolean isFallback,
    ZonedDateTime createdAt,
    ZonedDateTime updatedAt
) {

}
