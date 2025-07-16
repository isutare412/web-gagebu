package me.redshore.web_gagebu.feature.accountbook.dto;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.lang.Nullable;

public record AccountBookDto(
    UUID id,
    String name,
    List<MemberDto> members,
    List<CategoryDto> categories,
    @Nullable UUID createdBy,
    ZonedDateTime createdAt,
    ZonedDateTime updatedAt
) {

}
