package me.redshore.web_gagebu.feature.accountbook.repository;

import java.util.UUID;
import me.redshore.web_gagebu.feature.accountbook.domain.AccountBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountBookRepository extends JpaRepository<AccountBook, UUID> {

}
