package me.redshore.web_gagebu.accountbook.member.validator;

import java.util.Optional;
import java.util.UUID;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import me.redshore.web_gagebu.accountbook.member.MemberRepository;
import me.redshore.web_gagebu.accountbook.member.MemberRole;
import me.redshore.web_gagebu.common.exception.AppException;
import me.redshore.web_gagebu.common.exception.ErrorCode;
import me.redshore.web_gagebu.user.dto.UserJwtPayload;

@Component
@RequiredArgsConstructor
public class MemberValidator {

    private final MemberRepository memberRepository;

    @Transactional(readOnly = true)
    public void checkUserIsMemberOfAccountBook(UUID accountBookId) {
        final UUID userId = getCurrentUserId();
        if (!this.memberRepository.existsByAccountBookIdAndUserId(accountBookId, userId)) {
            throw new AppException(ErrorCode.FORBIDDEN,
                String.format("User with ID '%s' is not a member of account book with ID '%s'",
                              userId, accountBookId));
        }
    }

    @Transactional(readOnly = true)
    public void checkUserIsOwnerOfAccountBook(UUID accountBookId) {
        final UUID userId = getCurrentUserId();
        if (!this.memberRepository.existsByAccountBookIdAndUserIdAndRole(accountBookId, userId,
                                                                         MemberRole.OWNER)) {
            throw new AppException(ErrorCode.FORBIDDEN,
                String.format("User with ID '%s' is not a member of account book with ID '%s'",
                              userId, accountBookId));
        }
    }

    private UUID getCurrentUserId() {
        return Optional
            .ofNullable(SecurityContextHolder.getContext().getAuthentication().getPrincipal())
            .filter(UserJwtPayload.class::isInstance)
            .map(UserJwtPayload.class::cast)
            .map(UserJwtPayload::getId)
            .orElseThrow(() -> new AppException(ErrorCode.UNAUTHORIZED,
                "User is not authenticated"));
    }

}
