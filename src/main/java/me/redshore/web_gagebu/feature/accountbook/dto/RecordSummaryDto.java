package me.redshore.web_gagebu.feature.accountbook.dto;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.UUID;
import me.redshore.web_gagebu.feature.accountbook.domain.RecordType;

public record RecordSummaryDto(
    UUID id,
    String userNickname,
    String userPictureUrl,
    String category,
    RecordType recordType,
    long amount,
    String summary,
    LocalDate date,
    ZonedDateTime createdAt,
    ZonedDateTime updatedAt
) {

}
