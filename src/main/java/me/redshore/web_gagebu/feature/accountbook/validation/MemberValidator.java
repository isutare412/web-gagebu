package me.redshore.web_gagebu.feature.accountbook.validation;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import me.redshore.web_gagebu.common.error.AppException;
import me.redshore.web_gagebu.common.error.ErrorCode;
import me.redshore.web_gagebu.feature.accountbook.domain.MemberRole;
import me.redshore.web_gagebu.feature.accountbook.repository.MemberRepository;
import me.redshore.web_gagebu.feature.accountbook.repository.RecordRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class MemberValidator {

    private final MemberRepository memberRepository;
    private final RecordRepository recordRepository;

    @Transactional(readOnly = true)
    public void checkUserIsMemberOfAccountBook(UUID userId, UUID accountBookId) {
        if (!this.memberRepository.existsByAccountBookIdAndUserId(accountBookId, userId)) {
            throw new AppException(ErrorCode.FORBIDDEN,
                                   String.format(
                                       "User with ID '%s' is not a member of account book with ID '%s'",
                                       userId, accountBookId));
        }
    }

    @Transactional(readOnly = true)
    public void checkUserIsOwnerOfAccountBook(UUID userId, UUID accountBookId) {
        if (!this.memberRepository.existsByAccountBookIdAndUserIdAndRole(accountBookId, userId,
                                                                         MemberRole.OWNER)) {
            throw new AppException(ErrorCode.FORBIDDEN,
                                   String.format(
                                       "User with ID '%s' is not a member of account book with ID '%s'",
                                       userId, accountBookId));
        }
    }

    @Transactional(readOnly = true)
    public void checkUserCanModifyRecord(UUID accountBookId, UUID recordId, UUID userId) {
        final var member = this.memberRepository
            .findByAccountBookIdAndUserId(accountBookId, userId)
            .orElseThrow(() -> new AppException(ErrorCode.FORBIDDEN, String.format(
                "User with ID '%s' is not a member of account book with ID '%s'", userId,
                accountBookId)));

        final var record = this.recordRepository
            .findById(recordId)
            .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND,
                                                String.format("Record with ID '%s' not found",
                                                              recordId)));

        if (member.getRole() != MemberRole.OWNER) {
            if (!record.getUser().getId().equals(userId)) {
                throw new AppException(ErrorCode.FORBIDDEN,
                                       "Cannot modify record that does not belong to the user");
            }
        }
    }

}
