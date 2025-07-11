package me.redshore.web_gagebu.feature.accountbook.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;
import java.util.List;
import me.redshore.web_gagebu.feature.accountbook.domain.RecordType;
import me.redshore.web_gagebu.feature.accountbook.dto.SortDirection;
import org.springframework.lang.Nullable;

public record RecordListRequest(
    @Schema(example = "1")
    @NotNull(message = "Page number must not be null")
    @Positive(message = "Page number must be greater than 0")
    Integer page,

    @Schema(example = "20")
    @NotNull(message = "Page size must not be null")
    @Positive(message = "Page size must be greater than 0")
    Integer pageSize,

    @Nullable
    List<String> categories,

    @Nullable
    RecordType recordType,

    @Nullable
    LocalDate startDate,

    @Nullable
    LocalDate endDate,

    @Schema(example = "DESCENDING")
    @NotNull(message = "Sort direction must not be null")
    SortDirection direction
) {

    public RecordListRequest {
        if (categories == null) {
            categories = List.of();
        }
    }

}
