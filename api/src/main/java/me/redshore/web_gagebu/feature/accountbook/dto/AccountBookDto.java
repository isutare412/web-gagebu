package me.redshore.web_gagebu.feature.accountbook.dto;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

public record AccountBookDto(
    UUID id,
    String name,
    List<MemberDto> members,
    List<CategoryDto> categories,
    ZonedDateTime createdAt,
    ZonedDateTime updatedAt
) {

}
