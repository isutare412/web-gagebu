package me.redshore.web_gagebu.feature.accountbook.repository;

import java.util.UUID;
import me.redshore.web_gagebu.feature.accountbook.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {

}
