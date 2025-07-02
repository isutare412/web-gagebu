package me.redshore.web_gagebu.feature.accountbook.dto;

import java.time.LocalDate;
import java.util.UUID;
import me.redshore.web_gagebu.feature.accountbook.domain.RecordType;

public record RecordUpdateCommand(
    UUID recordId,
    UUID categoryId,
    RecordType recordType,
    long amount,
    String summary,
    String description,
    LocalDate date
) {

}
