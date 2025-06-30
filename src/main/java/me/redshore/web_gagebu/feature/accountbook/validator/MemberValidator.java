package me.redshore.web_gagebu.feature.accountbook.validator;

import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import me.redshore.web_gagebu.common.error.AppException;
import me.redshore.web_gagebu.common.error.ErrorCode;
import me.redshore.web_gagebu.feature.accountbook.domain.MemberRole;
import me.redshore.web_gagebu.feature.accountbook.repository.MemberRepository;
import me.redshore.web_gagebu.feature.auth.jwt.JwtUserPayload;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class MemberValidator {

    private final MemberRepository memberRepository;

    @Transactional(readOnly = true)
    public void checkUserIsMemberOfAccountBook(UUID accountBookId) {
        final UUID userId = getCurrentUserId();
        if (!this.memberRepository.existsByAccountBookIdAndUserId(accountBookId, userId)) {
            throw new AppException(ErrorCode.FORBIDDEN,
                                   String.format(
                                       "User with ID '%s' is not a member of account book with ID '%s'",
                                       userId, accountBookId));
        }
    }

    @Transactional(readOnly = true)
    public void checkUserIsOwnerOfAccountBook(UUID accountBookId) {
        final UUID userId = getCurrentUserId();
        if (!this.memberRepository.existsByAccountBookIdAndUserIdAndRole(accountBookId, userId,
                                                                         MemberRole.OWNER)) {
            throw new AppException(ErrorCode.FORBIDDEN,
                                   String.format(
                                       "User with ID '%s' is not a member of account book with ID '%s'",
                                       userId, accountBookId));
        }
    }

    private UUID getCurrentUserId() {
        return Optional
            .ofNullable(SecurityContextHolder.getContext().getAuthentication().getPrincipal())
            .filter(JwtUserPayload.class::isInstance)
            .map(JwtUserPayload.class::cast)
            .map(JwtUserPayload::getId)
            .orElseThrow(() -> new AppException(ErrorCode.UNAUTHORIZED,
                                                "User is not authenticated"));
    }
}
