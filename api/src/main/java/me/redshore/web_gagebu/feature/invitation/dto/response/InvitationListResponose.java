package me.redshore.web_gagebu.feature.invitation.dto.response;

import java.util.List;

public record InvitationListResponose(
    List<InvitationView> invitations
) {

}
