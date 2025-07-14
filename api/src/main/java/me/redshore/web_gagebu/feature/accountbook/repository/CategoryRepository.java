package me.redshore.web_gagebu.feature.accountbook.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import me.redshore.web_gagebu.feature.accountbook.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {

    @Query("SELECT c.id FROM Category c WHERE c.accountBook.id = :accountBookId AND c.name IN :names")
    List<UUID> findIdsByAccountBookIdAndNameIn(@Param("accountBookId") UUID accountBookId,
                                               @Param("names") List<String> names);

    List<Category> findAllByAccountBookIdOrderByCreatedAtAsc(UUID accountBookId);

    void deleteAllByAccountBookIdAndIsBasicFalse(UUID accountBookId);

    void deleteAllByAccountBookIdAndIsBasicFalseAndIdNotIn(UUID accountBookId, List<UUID> categoryIds);

    Optional<Category> findByAccountBookIdAndIsFallbackTrue(UUID accountBookId);

}
