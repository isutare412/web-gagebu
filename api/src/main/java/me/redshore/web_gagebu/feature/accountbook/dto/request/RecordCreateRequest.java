package me.redshore.web_gagebu.feature.accountbook.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;
import java.util.UUID;
import me.redshore.web_gagebu.feature.accountbook.domain.RecordType;
import me.redshore.web_gagebu.feature.accountbook.validation.RecordDescriptionValidation;
import me.redshore.web_gagebu.feature.accountbook.validation.RecordSummaryValidation;

public record RecordCreateRequest(
    @NotNull(message = "Category ID must not be null")
    UUID categoryId,

    @NotNull(message = "Record type must not be null")
    RecordType recordType,

    @Schema(example = "4000")
    @Positive(message = "Amount must be a positive number")
    long amount,

    @Schema(example = "Five Guys")
    @RecordSummaryValidation
    String summary,

    @Schema(example = "Hang out with Alice and Bob")
    @RecordDescriptionValidation
    String description,

    @NotNull(message = "Date must not be null")
    LocalDate date
) {

}
