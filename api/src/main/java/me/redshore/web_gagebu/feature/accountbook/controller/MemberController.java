package me.redshore.web_gagebu.feature.accountbook.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import me.redshore.web_gagebu.common.config.OpenApiConfig;
import me.redshore.web_gagebu.feature.accountbook.dto.MemberUpdateCommand;
import me.redshore.web_gagebu.feature.accountbook.dto.request.MemberUpdateRequest;
import me.redshore.web_gagebu.feature.accountbook.dto.response.MemberListResponse;
import me.redshore.web_gagebu.feature.accountbook.dto.response.MemberView;
import me.redshore.web_gagebu.feature.accountbook.mapping.MemberMapper;
import me.redshore.web_gagebu.feature.accountbook.service.MemberService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@SecurityRequirements({
    @SecurityRequirement(name = OpenApiConfig.BEARER_TOKEN_AUTH),
    @SecurityRequirement(name = OpenApiConfig.COOKIE_TOKEN_AUTH)})
@Tag(name = "Member")
public class MemberController {

    private final MemberService memberService;
    private final MemberMapper memberMapper;

    @GetMapping("/account-books/{accountBookId}/members/{memberId}")
    @Operation(summary = "Get member by ID")
    public MemberView getMember(@PathVariable UUID accountBookId, @PathVariable UUID memberId) {
        final var memberDto = this.memberService.getMember(accountBookId, memberId);
        return this.memberMapper.toView(memberDto);
    }

    @GetMapping("/account-books/{accountBookId}/members")
    @Operation(summary = "List members")
    public MemberListResponse listMembers(@PathVariable UUID accountBookId) {
        final var memberDtos = this.memberService.listMembers(accountBookId);
        final var memberView = memberDtos.stream()
                                         .map(this.memberMapper::toView)
                                         .toList();

        return MemberListResponse.builder()
                                 .members(memberView)
                                 .build();
    }

    @PutMapping("/account-books/{accountBookId}/members/{memberId}")
    @Operation(summary = "Update member role")
    public MemberView updateMember(@PathVariable UUID accountBookId, @PathVariable UUID memberId,
                                   @RequestBody MemberUpdateRequest body) {
        final var updateCommand = MemberUpdateCommand.builder()
                                                     .accountBookId(accountBookId)
                                                     .memberId(memberId)
                                                     .role(body.role())
                                                     .build();

        final var memberDto = this.memberService.updateMember(updateCommand);
        return this.memberMapper.toView(memberDto);
    }

}
