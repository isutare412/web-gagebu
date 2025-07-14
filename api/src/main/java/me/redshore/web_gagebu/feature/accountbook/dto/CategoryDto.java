package me.redshore.web_gagebu.feature.accountbook.dto;

import java.time.ZonedDateTime;
import java.util.UUID;

public record CategoryDto(
    UUID id,
    String name,
    Boolean isBasic,
    Boolean isFallback,
    ZonedDateTime createdAt,
    ZonedDateTime updatedAt
) {

}
