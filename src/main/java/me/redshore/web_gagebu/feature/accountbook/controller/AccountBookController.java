package me.redshore.web_gagebu.feature.accountbook.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import me.redshore.web_gagebu.common.config.OpenApiConfig;
import me.redshore.web_gagebu.common.error.AppException;
import me.redshore.web_gagebu.common.error.ErrorCode;
import me.redshore.web_gagebu.feature.accountbook.dto.AccountBookCreateCommand;
import me.redshore.web_gagebu.feature.accountbook.dto.AccountBookUpdateCommand;
import me.redshore.web_gagebu.feature.accountbook.dto.request.AccountBookCreateRequest;
import me.redshore.web_gagebu.feature.accountbook.dto.request.AccountBookUpdateRequest;
import me.redshore.web_gagebu.feature.accountbook.dto.response.AccountBookListResponse;
import me.redshore.web_gagebu.feature.accountbook.dto.response.AccountBookView;
import me.redshore.web_gagebu.feature.accountbook.mapping.AccountBookMapper;
import me.redshore.web_gagebu.feature.accountbook.service.AccountBookService;
import me.redshore.web_gagebu.feature.accountbook.validation.MemberValidator;
import me.redshore.web_gagebu.feature.auth.jwt.JwtUserPayload;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "AccountBook")
@SecurityRequirements({
    @SecurityRequirement(name = OpenApiConfig.BEARER_TOKEN_AUTH),
    @SecurityRequirement(name = OpenApiConfig.COOKIE_TOKEN_AUTH)})
public class AccountBookController {

    private final AccountBookService accountBookService;
    private final AccountBookMapper accountBookMapper;
    private final MemberValidator memberValidator;

    @GetMapping("/account-books/{accountBookId}")
    @Operation(summary = "Get account book by ID")
    public AccountBookView getAccountBook(@AuthenticationPrincipal JwtUserPayload jwtUserPayload,
                                          @PathVariable UUID accountBookId) {
        this.memberValidator.checkUserIsMemberOfAccountBook(jwtUserPayload.getId(), accountBookId);

        final var accountBookDto = this.accountBookService.getAccountBook(accountBookId);
        return this.accountBookMapper.toView(accountBookDto);
    }

    @GetMapping("/account-books")
    @Operation(summary = "List account books of user")
    public AccountBookListResponse listAccountBooks(
        @AuthenticationPrincipal JwtUserPayload jwtUserPayload) {
        final var accountBookViews =
            this.accountBookService.listAccountBooksOfUser(jwtUserPayload.getId())
                                   .stream()
                                   .map(this.accountBookMapper::toSummaryView)
                                   .toList();

        return new AccountBookListResponse(accountBookViews);
    }

    @PostMapping("/account-books")
    @Operation(summary = "Create account book")
    public AccountBookView createAccountBook(@AuthenticationPrincipal JwtUserPayload jwtUserPayload,
                                             @Valid @RequestBody AccountBookCreateRequest body) {
        return Optional.of(AccountBookCreateCommand.builder()
                                                   .userId(jwtUserPayload.getId())
                                                   .accountBookName(body.name())
                                                   .build())
                       .map(this.accountBookService::createAccountBook)
                       .map(this.accountBookMapper::toView)
                       .orElseThrow(() -> new AppException(ErrorCode.INTERNAL_SERVER_ERROR,
                                                           "Failed to create account book"));
    }

    @PutMapping("/account-books/{accountBookId}")
    @Operation(summary = "Update account book by ID")
    public AccountBookView updateAccountBook(@AuthenticationPrincipal JwtUserPayload jwtUserPayload,
                                             @PathVariable UUID accountBookId,
                                             @Valid @RequestBody AccountBookUpdateRequest body) {
        this.memberValidator.checkUserIsOwnerOfAccountBook(jwtUserPayload.getId(), accountBookId);

        return Optional.of(AccountBookUpdateCommand.builder()
                                                   .accountBookId(accountBookId)
                                                   .accountBookName(body.name())
                                                   .build())
                       .map(this.accountBookService::updateAccountBook)
                       .map(this.accountBookMapper::toView)
                       .orElseThrow(() -> new AppException(ErrorCode.INTERNAL_SERVER_ERROR,
                                                           "Failed to update account book"));
    }

    @DeleteMapping("/account-books/{accountBookId}")
    @Operation(summary = "Delete account book by ID")
    public void deleteAccountBook(@AuthenticationPrincipal JwtUserPayload jwtUserPayload,
                                  @PathVariable UUID accountBookId) {
        this.memberValidator.checkUserIsOwnerOfAccountBook(jwtUserPayload.getId(), accountBookId);

        this.accountBookService.deleteAccountBook(accountBookId);
    }

}
