package me.redshore.web_gagebu.feature.accountbook.repository;

import java.time.ZonedDateTime;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import me.redshore.web_gagebu.feature.accountbook.domain.Invitation;

@Repository
public interface InvitationRepository extends JpaRepository<Invitation, UUID> {

    public long deleteByExpirationBefore(ZonedDateTime expireation);

}
