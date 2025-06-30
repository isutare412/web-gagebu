package me.redshore.web_gagebu.feature.accountbook.service;

import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AccountBookService {

    private final CategoryProperties categoryProperties;

    private final EntityManager entityManager;
    private final AccountBookRepository accountBookRepository;
    private final MemberRepository memberRepository;
    private final UserRepository userRepository;
    private final AccountBookMapper accountBookMapper;

    @Transactional(readOnly = true)
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
        return Optional.of(this.memberRepository.findAllByUserId(userId))
                       .map(members ->
                                members.stream()
                                       .map(Member::getAccountBook)
                                       .map(this.accountBookMapper::toSummaryDto)
                                       .toList())
                       .orElseGet(List::of);
    }

    @Transactional
    public AccountBookDto createAccountBook(AccountBookCreateCommand command) {
        return this.userRepository
            .findById(command.userId())
            .map(user -> {
                final var member = buildMember(user);
                final var defaultCategories = buildDefaultCategories();
                var accountBook = AccountBook.builder()
                                             .name(command.accountBookName())
                                             .build();
                accountBook.addMember(member);
                accountBook.addCategories(defaultCategories);

                accountBook = this.accountBookRepository.save(accountBook);
                return accountBook;
            })
            .map(this.accountBookMapper::toDto)
            .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND,
                                                String.format("User with ID '%s' not found",
                                                              command.userId())));
    }

    @Transactional
    public AccountBookDto updateAccountBook(AccountBookUpdateCommand command) {
        return this.accountBookRepository
            .findById(command.accountBookId())
            .map(accountBook -> {
                mergeAccountBook(accountBook, command);
                this.entityManager.flush();
                return accountBook;
            })
            .map(this.accountBookMapper::toDto)
            .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND,
                                                String.format("Account book with ID '%s' not found",
                                                              command.accountBookId())));
    }

    @Transactional
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
                                      .map(categoryName -> Category.builder()
                                                                   .name(categoryName)
                                                                   .isBasic(true)
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
