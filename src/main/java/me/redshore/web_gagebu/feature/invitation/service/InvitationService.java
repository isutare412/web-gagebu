package me.redshore.web_gagebu.feature.invitation.service;

import java.time.ZonedDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.redshore.web_gagebu.common.config.properties.InvitationProperties;
import me.redshore.web_gagebu.common.error.AppException;
import me.redshore.web_gagebu.common.error.ErrorCode;
import me.redshore.web_gagebu.feature.accountbook.domain.AccountBook;
import me.redshore.web_gagebu.feature.accountbook.domain.Member;
import me.redshore.web_gagebu.feature.accountbook.domain.MemberRole;
import me.redshore.web_gagebu.feature.accountbook.repository.AccountBookRepository;
import me.redshore.web_gagebu.feature.invitation.domain.Invitation;
import me.redshore.web_gagebu.feature.invitation.dto.InvitationDto;
import me.redshore.web_gagebu.feature.invitation.dto.InvitationListResult;
import me.redshore.web_gagebu.feature.invitation.mapping.InvitationMapper;
import me.redshore.web_gagebu.feature.invitation.repository.InvitationRepository;
import me.redshore.web_gagebu.feature.user.domain.User;
import me.redshore.web_gagebu.feature.user.repository.UserRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class InvitationService {

    private final InvitationProperties invitationProperties;

    private final InvitationMapper invitationMapper;

    private final InvitationRepository invitationRepository;

    private final AccountBookRepository accountBookRepository;

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ADMIN') or @accountBookAuthorizer.canManage(#accountBookId)")
    public InvitationListResult listInvitations(UUID accountBookId) {
        final var invitationDtos = this.invitationRepository.findAllByAccountBookId(accountBookId)
                                                            .stream()
                                                            .map(this.invitationMapper::toDto)
                                                            .toList();

        return InvitationListResult.builder()
                                   .invitations(invitationDtos)
                                   .build();
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN') or @accountBookAuthorizer.canManage(#accountBookId)")
    public InvitationDto createInvitation(UUID accountBookId) {
        final var accountBook = this.accountBookRepository
            .findById(accountBookId)
            .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND,
                                                String.format("Account book with id %s not found",
                                                              accountBookId)));

        var invitation = Invitation.builder()
                                   .accountBook(accountBook)
                                   .expiration(getExpiry())
                                   .build();
        invitation = this.invitationRepository.save(invitation);

        return this.invitationMapper.toDto(invitation);
    }

    @Transactional
    public void joinInvitation(UUID invitationId, UUID userId) {
        final var user = this.userRepository
            .findById(userId)
            .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND,
                                                String.format("User with id %s not found",
                                                              userId)));

        final var invitation = this.invitationRepository
            .findById(invitationId)
            .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND,
                                                String.format("Invitation with id %s not found",
                                                              invitationId)));

        final var accountBook = invitation.getAccountBook();

        if (isUserAlreadyMember(accountBook, userId)) {
            throw new AppException(ErrorCode.CONFLICT, String.format(
                "User with id %s is already a member of account book %s", userId,
                accountBook.getId()));
        }

        joinMember(accountBook, user);
    }

    @Transactional
    public void cleanUpExpiredInvitations() {
        final var now = ZonedDateTime.now();
        final var count = invitationRepository.deleteByExpirationBefore(now);
        if (count > 0) {
            log.info("Removed {} expired invitations", count);
        }
    }

    private boolean isUserAlreadyMember(AccountBook accountBook, UUID userId) {
        return accountBook.getMembers()
                          .stream()
                          .anyMatch(m -> m.getUser().getId().equals(userId));
    }

    private void joinMember(AccountBook accountBook, User user) {
        final var member = Member.builder()
                                 .accountBook(accountBook)
                                 .user(user)
                                 .role(MemberRole.PARTICIPANT)
                                 .build();
        accountBook.addMember(member);
    }

    private ZonedDateTime getExpiry() {
        return ZonedDateTime.now().plus(invitationProperties.getExpiration());
    }

}
