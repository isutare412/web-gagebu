package me.redshore.web_gagebu.feature.accountbook.repository;

import java.time.ZonedDateTime;
import java.util.UUID;
import me.redshore.web_gagebu.feature.accountbook.domain.Invitation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvitationRepository extends JpaRepository<Invitation, UUID> {

    long deleteByExpirationBefore(ZonedDateTime expireation);
}
