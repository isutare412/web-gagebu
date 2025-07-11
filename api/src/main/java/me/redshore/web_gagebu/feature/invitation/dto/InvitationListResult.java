package me.redshore.web_gagebu.feature.invitation.dto;

import java.util.List;
import lombok.Builder;

@Builder
public record InvitationListResult(
    List<InvitationDto> invitations
) {

}
