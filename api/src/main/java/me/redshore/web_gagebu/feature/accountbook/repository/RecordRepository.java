package me.redshore.web_gagebu.feature.accountbook.repository;

import java.util.List;
import java.util.UUID;
import me.redshore.web_gagebu.feature.accountbook.domain.Record;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RecordRepository extends JpaRepository<Record, UUID>, RecordQuerydslRepository {

    /**
     * Updates the category of records to a fallback category. It is preferable to use
     * {@link #updateCategoryToFallbackBatch(List, UUID)} for batch updates.
     * 
     * @param categoryIds
     * @param fallbackCategoryId
     */
    @Modifying
    @Query("UPDATE Record r SET r.category.id = :fallbackCategoryId WHERE r.category.id IN :categoryIds")
    void updateCategoryToFallback(@Param("categoryIds") List<UUID> categoryIds,
                                  @Param("fallbackCategoryId") UUID fallbackCategoryId);

    default void updateCategoryToFallbackBatch(List<UUID> categoryIds, UUID fallbackCategoryId) {
        final var batchSize = 1000;
        for (var i = 0; i < categoryIds.size(); i += batchSize) {
            int end = Math.min(i + batchSize, categoryIds.size());
            List<UUID> batch = categoryIds.subList(i, end);
            updateCategoryToFallback(batch, fallbackCategoryId);
        }
    }

}
