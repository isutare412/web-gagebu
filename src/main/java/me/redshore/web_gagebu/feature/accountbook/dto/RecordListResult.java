package me.redshore.web_gagebu.feature.accountbook.dto;

import java.util.List;
import lombok.Builder;

@Builder
public record RecordListResult(
    List<RecordSummaryDto> records,
    int page,
    int pageSize,
    int totalPages,
    long totalElements
) {

}
