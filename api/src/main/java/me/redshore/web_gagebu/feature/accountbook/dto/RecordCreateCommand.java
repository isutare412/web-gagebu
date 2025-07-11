package me.redshore.web_gagebu.feature.accountbook.dto;

import java.time.LocalDate;
import java.util.UUID;
import me.redshore.web_gagebu.feature.accountbook.domain.RecordType;

public record RecordCreateCommand(
    UUID userId,
    UUID accountBookId,
    UUID categoryId,
    RecordType recordType,
    long amount,
    String summary,
    String description,
    LocalDate date
) {

}
