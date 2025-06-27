package me.redshore.web_gagebu.feature.accountbook.repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import me.redshore.web_gagebu.feature.accountbook.domain.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {
}
