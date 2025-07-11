package me.redshore.web_gagebu.feature.accountbook.dto;

import java.time.ZonedDateTime;
import java.util.UUID;

public record AccountBookSummaryDto(
    UUID id,
    String name,
    ZonedDateTime createdAt,
    ZonedDateTime updatedAt
) {

}
