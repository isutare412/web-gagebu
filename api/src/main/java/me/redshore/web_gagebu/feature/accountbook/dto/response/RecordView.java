package me.redshore.web_gagebu.feature.accountbook.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.UUID;
import me.redshore.web_gagebu.feature.accountbook.domain.RecordType;

public record RecordView(
    UUID id,

    UUID userId,

    @Schema(example = "Bob")
    String userNickname,

    @Schema(example = "https://example.com/thumbnail.png")
    String userPictureUrl,

    String category,
    RecordType recordType,

    @Schema(example = "4000")
    long amount,

    @Schema(example = "Five Guys")
    String summary,

    @Schema(example = "Hang out with Alice and Bob")
    String description,

    LocalDate date,
    ZonedDateTime createdAt,
    ZonedDateTime updatedAt
) {

}
