package me.redshore.web_gagebu.feature.accountbook.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.Builder;
import me.redshore.web_gagebu.feature.accountbook.domain.RecordType;
import org.springframework.lang.Nullable;

@Builder
public record RecordListQuery(
    UUID accountBookId,
    int page,
    int pageSize,
    List<String> categories,
    @Nullable RecordType recordType,
    @Nullable LocalDate startDate,
    @Nullable LocalDate endDate,
    SortDirection sortDirection
) {

}
