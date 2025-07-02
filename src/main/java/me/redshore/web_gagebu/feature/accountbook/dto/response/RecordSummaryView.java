package me.redshore.web_gagebu.feature.accountbook.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.UUID;
import me.redshore.web_gagebu.feature.accountbook.domain.RecordType;

public record RecordSummaryView(
    UUID id,
    @Schema(example = "Bob") String userNickname,
    @Schema(example = "https://example.com/thumbnail.png") String userPictureUrl,
    @Schema(example = "Foods") String category,
    RecordType recordType,
    @Schema(example = "4000") long amount,
    @Schema(example = "Five Guys") String summary,
    LocalDate date,
    ZonedDateTime createdAt,
    ZonedDateTime updatedAt
) {

}
