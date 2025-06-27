package me.redshore.web_gagebu.feature.accountbook.service;

import java.time.ZonedDateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.redshore.web_gagebu.feature.accountbook.repository.InvitationRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class InvitationService {

    private final InvitationRepository invitationRepository;

    @Transactional
    public void cleanUpExpiredInvitations() {
        var now = ZonedDateTime.now();

        long count = invitationRepository.deleteByExpirationBefore(now);
        if (count > 0) {
            log.info("Removed {} expired invitations", count);
        }
    }

}
