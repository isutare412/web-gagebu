package me.redshore.web_gagebu.feature.accountbook.dto.response;

import java.util.List;

public record RecordListResponse(
    List<RecordSummaryView> records,
    int page,
    int pageSize,
    int totalPages,
    long totalElements
) {

}
