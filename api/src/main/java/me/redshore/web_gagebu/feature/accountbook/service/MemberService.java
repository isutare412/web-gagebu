package me.redshore.web_gagebu.feature.accountbook.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.redshore.web_gagebu.common.error.AppException;
import me.redshore.web_gagebu.common.error.ErrorCode;
import me.redshore.web_gagebu.feature.accountbook.dto.MemberDto;
import me.redshore.web_gagebu.feature.accountbook.dto.MemberUpdateCommand;
import me.redshore.web_gagebu.feature.accountbook.mapping.MemberMapper;
import me.redshore.web_gagebu.feature.accountbook.repository.AccountBookRepository;
import me.redshore.web_gagebu.feature.accountbook.repository.MemberRepository;
import me.redshore.web_gagebu.feature.accountbook.validation.MemberValidator;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;
    private final AccountBookRepository accountBookRepository;
    private final MemberValidator memberValidator;
    private final MemberMapper memberMapper;

    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ADMIN') or @memberAuthorizer.canAccess(#memberId)")
    public MemberDto getMember(UUID accountBookId, UUID memberId) {
        this.memberValidator.checkMemberContainedInAccountBook(accountBookId, memberId);

        final var member = this.memberRepository
            .findById(memberId)
            .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND,
                                                String.format("Member %s not found", memberId)));
        return this.memberMapper.toDto(member);
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ADMIN') or @accountBookAuthorizer.canAccess(#accountBookId)")
    public List<MemberDto> listMembers(UUID accountBookId) {
        return this.memberRepository.findAllByAccountBookId(accountBookId)
                                    .stream()
                                    .map(this.memberMapper::toDto)
                                    .toList();
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN') or @memberAuthorizer.canManage(#command.memberId)")
    public MemberDto updateMember(MemberUpdateCommand command) {
        this.memberValidator.checkMemberContainedInAccountBook(command.accountBookId(),
                                                               command.memberId());

        var member = this.memberRepository
            .findById(command.memberId())
            .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND,
                                                String.format("Member %s not found",
                                                              command.memberId())));
        final var user = member.getUser();

        final var accountBook = this.accountBookRepository
            .findById(command.accountBookId())
            .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND,
                                                String.format("Account book %s not found",
                                                              command.accountBookId())));

        Optional.ofNullable(accountBook.getCreatedBy())
                .ifPresent(ownerId -> {
                    if (ownerId.equals(user.getId())) {
                        throw new AppException(ErrorCode.BAD_REQUEST,
                                               "Cannot change role of the account book owner");
                    }
                });

        member.setRole(command.role());
        member = this.memberRepository.save(member);
        return this.memberMapper.toDto(member);
    }

}
