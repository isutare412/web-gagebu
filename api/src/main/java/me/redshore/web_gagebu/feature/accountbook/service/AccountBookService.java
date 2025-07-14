package me.redshore.web_gagebu.feature.accountbook.service;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import me.redshore.web_gagebu.common.config.properties.CategoryProperties;
import me.redshore.web_gagebu.common.error.AppException;
import me.redshore.web_gagebu.common.error.ErrorCode;
import me.redshore.web_gagebu.feature.accountbook.domain.AccountBook;
import me.redshore.web_gagebu.feature.accountbook.domain.Category;
import me.redshore.web_gagebu.feature.accountbook.domain.Member;
import me.redshore.web_gagebu.feature.accountbook.domain.MemberRole;
import me.redshore.web_gagebu.feature.accountbook.dto.AccountBookCreateCommand;
import me.redshore.web_gagebu.feature.accountbook.dto.AccountBookDto;
import me.redshore.web_gagebu.feature.accountbook.dto.AccountBookSummaryDto;
import me.redshore.web_gagebu.feature.accountbook.dto.AccountBookUpdateCommand;
import me.redshore.web_gagebu.feature.accountbook.mapping.AccountBookMapper;
import me.redshore.web_gagebu.feature.accountbook.repository.AccountBookRepository;
import me.redshore.web_gagebu.feature.accountbook.repository.MemberRepository;
import me.redshore.web_gagebu.feature.user.domain.User;
import me.redshore.web_gagebu.feature.user.repository.UserRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AccountBookService {

    private final CategoryProperties categoryProperties;

    private final AccountBookRepository accountBookRepository;
    private final MemberRepository memberRepository;
    private final UserRepository userRepository;
    private final AccountBookMapper accountBookMapper;

    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ADMIN') or @accountBookAuthorizer.canAccess(#id)")
    public AccountBookDto getAccountBook(UUID id) {
        return this.accountBookRepository
            .findById(id)
            .map(this.accountBookMapper::toDto)
            .orElseThrow(
                () -> new AppException(ErrorCode.NOT_FOUND,
                                       String.format("Account book with ID '%s' not found", id)));
    }

    @Transactional(readOnly = true)
    public List<AccountBookSummaryDto> listAccountBooksOfUser(UUID userId) {
        return this.memberRepository.findAllByUserId(userId)
                                    .stream()
                                    .map(Member::getAccountBook)
                                    .map(this.accountBookMapper::toSummaryDto)
                                    .toList();
    }

    @Transactional
    public AccountBookDto createAccountBook(AccountBookCreateCommand command) {
        return this.userRepository
            .findById(command.userId())
            .map(user -> {
                var accountBook = AccountBook.builder()
                                             .name(command.accountBookName())
                                             .build();
                accountBook.addMember(buildMember(user));
                accountBook.addCategories(buildDefaultCategories());

                accountBook = this.accountBookRepository.save(accountBook);
                return accountBook;
            })
            .map(this.accountBookMapper::toDto)
            .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND,
                                                String.format("User with ID '%s' not found",
                                                              command.userId())));
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN') or @accountBookAuthorizer.canManage(#command.accountBookId)")
    public AccountBookDto updateAccountBook(AccountBookUpdateCommand command) {
        return this.accountBookRepository
            .findById(command.accountBookId())
            .map(accountBook -> {
                mergeAccountBook(accountBook, command);
                return accountBook;
            })
            .map(this.accountBookMapper::toDto)
            .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND,
                                                String.format("Account book with ID '%s' not found",
                                                              command.accountBookId())));
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN') or @accountBookAuthorizer.canManage(#id)")
    public void deleteAccountBook(UUID id) {
        this.accountBookRepository.deleteById(id);
    }

    private void mergeAccountBook(AccountBook accountBook,
                                  AccountBookUpdateCommand command) {
        accountBook.setName(command.accountBookName());
    }

    private List<Category> buildDefaultCategories() {
        return this.categoryProperties.getDefaults()
                                      .stream()
                                      .map(category -> Category.builder()
                                                               .name(category.getName())
                                                               .isBasic(true)
                                                               .isFallback(category.getIsFallback())
                                                               .build())
                                      .toList();
    }

    private Member buildMember(User user) {
        return Member.builder()
                     .user(user)
                     .role(MemberRole.OWNER)
                     .build();
    }

}
