package me.redshore.web_gagebu.feature.accountbook.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import me.redshore.web_gagebu.common.config.properties.InvitationProperties;
import me.redshore.web_gagebu.feature.accountbook.service.InvitationService;

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
