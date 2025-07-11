package me.redshore.web_gagebu.feature.auth.authorizer;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import me.redshore.web_gagebu.feature.accountbook.domain.MemberRole;
import me.redshore.web_gagebu.feature.accountbook.repository.MemberRepository;
import me.redshore.web_gagebu.feature.auth.jwt.JwtUserPayload;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class AccountBookAuthorizer {

    private final MemberRepository memberRepository;

    @Transactional(readOnly = true)
    public boolean canAccess(UUID accountBookId) {
        final var jwtUserPayload = JwtUserPayload.fromSecurityContext();
        return this.memberRepository.existsByAccountBookIdAndUserId(accountBookId,
                                                                    jwtUserPayload.getId());
    }

    @Transactional(readOnly = true)
    public boolean canManage(UUID accountBookId) {
        final var jwtUserPayload = JwtUserPayload.fromSecurityContext();
        return this.memberRepository.existsByAccountBookIdAndUserIdAndRole(accountBookId,
                                                                           jwtUserPayload.getId(),
                                                                           MemberRole.OWNER);
    }

}
