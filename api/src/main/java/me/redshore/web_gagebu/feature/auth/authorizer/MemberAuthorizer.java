package me.redshore.web_gagebu.feature.auth.authorizer;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import me.redshore.web_gagebu.common.error.AppException;
import me.redshore.web_gagebu.common.error.ErrorCode;
import me.redshore.web_gagebu.feature.accountbook.domain.MemberRole;
import me.redshore.web_gagebu.feature.accountbook.repository.MemberRepository;
import me.redshore.web_gagebu.feature.auth.jwt.JwtUserPayload;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class MemberAuthorizer {

    private final MemberRepository memberRepository;

    @Transactional(readOnly = true)
    public boolean canAccess(UUID memberId) {
        final var jwtUserPayload = JwtUserPayload.fromSecurityContext();

        final var member = this.memberRepository
            .findById(memberId)
            .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND,
                                                String.format("Member %s not found", memberId)));

        final var accountBookId = member.getAccountBook().getId();

        return this.memberRepository.existsByAccountBookIdAndUserId(accountBookId,
                                                                    jwtUserPayload.getId());
    }

    @Transactional(readOnly = true)
    public boolean canManage(UUID memberId) {
        final var jwtUserPayload = JwtUserPayload.fromSecurityContext();

        final var member = this.memberRepository
            .findById(memberId)
            .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND,
                                                String.format("Member %s not found", memberId)));

        final var accountBookId = member.getAccountBook().getId();

        return this.memberRepository.existsByAccountBookIdAndUserIdAndRole(accountBookId,
                                                                           jwtUserPayload.getId(),
                                                                           MemberRole.OWNER);
    }

}
