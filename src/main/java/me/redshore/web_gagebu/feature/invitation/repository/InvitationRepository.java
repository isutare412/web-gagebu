package me.redshore.web_gagebu.feature.invitation.repository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import me.redshore.web_gagebu.feature.invitation.domain.Invitation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvitationRepository extends JpaRepository<Invitation, UUID> {

    List<Invitation> findAllByAccountBookId(UUID accountBookId);

    long deleteByExpirationBefore(ZonedDateTime expireation);

}
