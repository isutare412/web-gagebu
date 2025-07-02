package me.redshore.web_gagebu.feature.accountbook.repository;

import static me.redshore.web_gagebu.feature.accountbook.domain.QRecord.record;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import me.redshore.web_gagebu.common.error.AppException;
import me.redshore.web_gagebu.common.error.ErrorCode;
import me.redshore.web_gagebu.feature.accountbook.domain.Record;
import me.redshore.web_gagebu.feature.accountbook.dto.RecordListQuery;
import me.redshore.web_gagebu.feature.accountbook.dto.SortDirection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@RequiredArgsConstructor
public class RecordQuerydslRepositoryImpl implements RecordQuerydslRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Record> findAllByQuery(RecordListQuery query, List<UUID> categoryIds) {
        final var pageable = PageRequest.of(query.page(), query.pageSize());

        final var predicates = new Predicate[]{
            record.accountBook.id.eq(query.accountBookId()),
            !categoryIds.isEmpty() ? record.category.id.in(categoryIds) : null,
            query.recordType() != null ? record.recordType.eq(query.recordType()) : null,
            query.startDate() != null ? record.date.goe(query.startDate()) : null,
            query.endDate() != null ? record.date.loe(query.endDate()) : null
        };

        final var records = queryFactory
            .selectFrom(record)
            .where(predicates)
            .orderBy(buildOrderSpecifiers(query.sortDirection()))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        final long totalCount = Optional
            .ofNullable(queryFactory.select(record.count())
                                    .from(record)
                                    .where(predicates)
                                    .fetchOne())
            .orElseThrow(
                () -> new AppException(ErrorCode.INTERNAL_SERVER_ERROR, "Failed to count records"));

        return new PageImpl<>(records, pageable, totalCount);
    }

    private OrderSpecifier<?>[] buildOrderSpecifiers(SortDirection direction) {
        return switch (direction) {
            case SortDirection.ASCENDING ->
                new OrderSpecifier[]{record.date.asc(), record.createdAt.asc()};
            case SortDirection.DESCENDING ->
                new OrderSpecifier[]{record.date.desc(), record.createdAt.desc()};
        };
    }

}
