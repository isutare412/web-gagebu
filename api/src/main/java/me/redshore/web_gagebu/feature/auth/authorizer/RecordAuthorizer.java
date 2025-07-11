package me.redshore.web_gagebu.feature.auth.authorizer;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import me.redshore.web_gagebu.common.error.AppException;
import me.redshore.web_gagebu.common.error.ErrorCode;
import me.redshore.web_gagebu.feature.accountbook.domain.MemberRole;
import me.redshore.web_gagebu.feature.accountbook.repository.MemberRepository;
import me.redshore.web_gagebu.feature.accountbook.repository.RecordRepository;
import me.redshore.web_gagebu.feature.auth.jwt.JwtUserPayload;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class RecordAuthorizer {

    private final MemberRepository memberRepository;
    private final RecordRepository recordRepository;

    @Transactional(readOnly = true)
    public boolean canModify(UUID accountBookId, UUID recordId) {
        final var jwtUserPayload = JwtUserPayload.fromSecurityContext();

        final var member = this.memberRepository
            .findByAccountBookIdAndUserId(accountBookId, jwtUserPayload.getId())
            .orElseThrow(() -> new AppException(ErrorCode.FORBIDDEN, String.format(
                "User with ID '%s' is not a member of account book with ID '%s'",
                jwtUserPayload.getId(), accountBookId)));

        final var record = this.recordRepository
            .findById(recordId)
            .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND,
                                                String.format("Record with ID '%s' not found",
                                                              recordId)));

        if (member.getRole() == MemberRole.OWNER) {
            return true;
        }

        return record.getUser().getId().equals(jwtUserPayload.getId());
    }

}
