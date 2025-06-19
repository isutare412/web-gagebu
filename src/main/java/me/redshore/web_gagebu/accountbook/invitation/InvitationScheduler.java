package me.redshore.web_gagebu.accountbook.invitation;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import me.redshore.web_gagebu.config.properties.InvitationProperties;

@Component
@RequiredArgsConstructor
public class InvitationScheduler {

    private final InvitationService invitationService;

    @SuppressWarnings("unused")
    private final InvitationProperties invitationProperties;

    @Scheduled(fixedRateString = "#{@invitationProperties.getCleanup().getRate().toMillis()}")
    void scheduleCleanUpExpiredInvitations() {
        invitationService.cleanUpExpiredInvitations();
    }

}
