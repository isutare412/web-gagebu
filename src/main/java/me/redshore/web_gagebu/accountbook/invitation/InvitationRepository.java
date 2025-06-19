package me.redshore.web_gagebu.accountbook.invitation;

import java.time.ZonedDateTime;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvitationRepository extends JpaRepository<Invitation, UUID> {

    public long deleteByExpirationBefore(ZonedDateTime expireation);

}
