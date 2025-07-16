package me.redshore.web_gagebu.feature.accountbook.validation;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import me.redshore.web_gagebu.common.error.AppException;
import me.redshore.web_gagebu.common.error.ErrorCode;
import me.redshore.web_gagebu.feature.accountbook.repository.MemberRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class MemberValidator {

    private final MemberRepository memberRepository;

    @Transactional(readOnly = true)
    public void checkMemberContainedInAccountBook(UUID accountBookId, UUID memberId) {
        final var member = this.memberRepository
            .findById(memberId)
            .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND,
                                                String.format("Member with ID '%s' not found",
                                                              memberId)));

        if (!member.getAccountBook().getId().equals(accountBookId)) {
            throw new AppException(ErrorCode.CONFLICT,
                                   String.format(
                                       "Member with ID '%s' does not belong to account book with ID '%s'",
                                       memberId, accountBookId));
        }
    }

}
