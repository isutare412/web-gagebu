package me.redshore.web_gagebu.feature.invitation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import me.redshore.web_gagebu.common.config.OpenApiConfig;
import me.redshore.web_gagebu.feature.auth.jwt.JwtUserPayload;
import me.redshore.web_gagebu.feature.invitation.dto.response.InvitationListResponose;
import me.redshore.web_gagebu.feature.invitation.dto.response.InvitationView;
import me.redshore.web_gagebu.feature.invitation.mapping.InvitationMapper;
import me.redshore.web_gagebu.feature.invitation.service.InvitationService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Invitation")
@SecurityRequirements({
    @SecurityRequirement(name = OpenApiConfig.BEARER_TOKEN_AUTH),
    @SecurityRequirement(name = OpenApiConfig.COOKIE_TOKEN_AUTH)})
public class InvitationController {

    private final InvitationService invitationService;
    private final InvitationMapper invitationMapper;

    @GetMapping("/account-books/{accountBookId}/invitations")
    @Operation(summary = "List current valid invitations for an account book")
    @Tag(name = "AccountBook")
    public InvitationListResponose listInvitations(@PathVariable UUID accountBookId) {
        final var listResult = this.invitationService.listInvitations(accountBookId);
        return this.invitationMapper.toListResponse(listResult);
    }

    @PostMapping("/account-books/{accountBookId}/invitations")
    @Operation(summary = "Create an invitation for an account book")
    @Tag(name = "AccountBook")
    public InvitationView createInvitation(@PathVariable UUID accountBookId) {
        final var invitationDto = this.invitationService.createInvitation(accountBookId);
        return this.invitationMapper.toView(invitationDto);
    }

    @PostMapping("/invitations/{invitationId}/join")
    @Operation(summary = "Join an account book using an invitation")
    public void joinInvitation(@AuthenticationPrincipal JwtUserPayload jwtUserPayload,
                               @PathVariable UUID invitationId) {
        this.invitationService.joinInvitation(invitationId, jwtUserPayload.getId());
    }

}
