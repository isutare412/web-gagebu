package me.redshore.web_gagebu.feature.invitation.scheduler;

import lombok.RequiredArgsConstructor;
import me.redshore.web_gagebu.feature.invitation.service.InvitationService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InvitationScheduler {

    private final InvitationService invitationService;

    @Scheduled(fixedRateString = "#{@invitationProperties.getCleanup().getRate().toMillis()}")
    void scheduleCleanUpExpiredInvitations() {
        invitationService.cleanUpExpiredInvitations();
    }

}
