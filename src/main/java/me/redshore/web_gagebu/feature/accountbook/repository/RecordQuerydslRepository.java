package me.redshore.web_gagebu.feature.accountbook.repository;

import java.util.List;
import java.util.UUID;
import me.redshore.web_gagebu.feature.accountbook.domain.Record;
import me.redshore.web_gagebu.feature.accountbook.dto.RecordListQuery;
import org.springframework.data.domain.Page;

public interface RecordQuerydslRepository {

    Page<Record> findAllByQuery(RecordListQuery query, List<UUID> categoryIds);

}
