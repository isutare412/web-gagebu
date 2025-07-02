package me.redshore.web_gagebu.feature.accountbook.repository;

import java.util.UUID;
import me.redshore.web_gagebu.feature.accountbook.domain.Record;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecordRepository extends JpaRepository<Record, UUID>, RecordQuerydslRepository {

}
