package me.redshore.web_gagebu.feature.invitation.service;

import java.time.ZonedDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.redshore.web_gagebu.feature.invitation.repository.InvitationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class InvitationService {

    private final InvitationRepository invitationRepository;

    @Transactional
    public void cleanUpExpiredInvitations() {
        final var now = ZonedDateTime.now();
        final var count = invitationRepository.deleteByExpirationBefore(now);
        if (count > 0) {
            log.info("Removed {} expired invitations", count);
        }
    }

}
