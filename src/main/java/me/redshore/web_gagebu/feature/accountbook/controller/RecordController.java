package me.redshore.web_gagebu.feature.accountbook.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import me.redshore.web_gagebu.common.config.OpenApiConfig;
import me.redshore.web_gagebu.feature.accountbook.domain.RecordType;
import me.redshore.web_gagebu.feature.accountbook.dto.RecordListQuery;
import me.redshore.web_gagebu.feature.accountbook.dto.SortDirection;
import me.redshore.web_gagebu.feature.accountbook.dto.request.RecordCreateRequest;
import me.redshore.web_gagebu.feature.accountbook.dto.request.RecordUpdateRequest;
import me.redshore.web_gagebu.feature.accountbook.dto.response.RecordListResponse;
import me.redshore.web_gagebu.feature.accountbook.dto.response.RecordView;
import me.redshore.web_gagebu.feature.accountbook.mapping.RecordMapper;
import me.redshore.web_gagebu.feature.accountbook.service.RecordService;
import me.redshore.web_gagebu.feature.accountbook.validation.MemberValidator;
import me.redshore.web_gagebu.feature.accountbook.validation.RecordValidator;
import me.redshore.web_gagebu.feature.auth.jwt.JwtUserPayload;
import org.springframework.lang.Nullable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@SecurityRequirements({
    @SecurityRequirement(name = OpenApiConfig.BEARER_TOKEN_AUTH),
    @SecurityRequirement(name = OpenApiConfig.COOKIE_TOKEN_AUTH)})
@Tag(name = "Record")
public class RecordController {

    private final RecordValidator recordValidator;
    private final MemberValidator memberValidator;
    private final RecordMapper recordMapper;
    private final RecordService recordService;

    @GetMapping("/account-books/{accountBookId}/records/{recordId}")
    @Operation(summary = "Get record by ID")
    public RecordView getRecord(@AuthenticationPrincipal JwtUserPayload jwtUserPayload,
                                @PathVariable UUID accountBookId, @PathVariable UUID recordId) {
        this.memberValidator.checkUserIsMemberOfAccountBook(jwtUserPayload.getId(), accountBookId);
        this.recordValidator.checkRecordContainedInAccountBook(accountBookId, recordId);

        final var recordDto = this.recordService.getRecordById(recordId);
        return this.recordMapper.toView(recordDto);
    }

    @GetMapping("/account-books/{accountBookId}/records")
    @Operation(summary = "List records of account book")
    public RecordListResponse listRecords(@AuthenticationPrincipal JwtUserPayload jwtUserPayload,
                                          @PathVariable UUID accountBookId,
                                          @RequestParam(defaultValue = "1") @Positive int page,
                                          @RequestParam(defaultValue = "20") @Positive int pageSize,
                                          @RequestParam @Nullable List<String> categories,
                                          @RequestParam @Nullable RecordType recordType,
                                          @RequestParam @Nullable LocalDate startDate,
                                          @RequestParam @Nullable LocalDate endDate,
                                          @RequestParam SortDirection direction) {
        this.memberValidator.checkUserIsMemberOfAccountBook(jwtUserPayload.getId(), accountBookId);
        this.recordValidator.checkDateRange(startDate, endDate);

        final var listQuery = RecordListQuery.builder()
                                             .accountBookId(accountBookId)
                                             .page(page - 1)
                                             .pageSize(pageSize)
                                             .categories(
                                                 categories != null ? categories : List.of())
                                             .recordType(recordType)
                                             .startDate(startDate)
                                             .endDate(endDate)
                                             .sortDirection(direction)
                                             .build();

        final var listResult = this.recordService.listRecords(listQuery);
        return this.recordMapper.toListResponse(listResult);
    }

    @PostMapping("/account-books/{accountBookId}/records")
    @Operation(summary = "Create a new record")
    public RecordView createRecord(@AuthenticationPrincipal JwtUserPayload jwtUserPayload,
                                   @PathVariable UUID accountBookId,
                                   @Valid @RequestBody RecordCreateRequest body) {
        this.memberValidator.checkUserIsMemberOfAccountBook(jwtUserPayload.getId(), accountBookId);

        final var createCommand = this.recordMapper.toCreateCommand(body, jwtUserPayload.getId(),
                                                                    accountBookId);
        final var recordDto = this.recordService.createRecord(createCommand);
        return this.recordMapper.toView(recordDto);
    }

    @PutMapping("/account-books/{accountBookId}/records/{recordId}")
    @Operation(summary = "Update an existing record")
    public RecordView updateRecord(@AuthenticationPrincipal JwtUserPayload jwtUserPayload,
                                   @PathVariable UUID accountBookId,
                                   @PathVariable UUID recordId,
                                   @Valid @RequestBody RecordUpdateRequest body) {
        this.memberValidator.checkUserCanModifyRecord(accountBookId, recordId,
                                                      jwtUserPayload.getId());
        this.recordValidator.checkRecordContainedInAccountBook(accountBookId, recordId);

        final var updateCommand = this.recordMapper.toUpdateCommand(body, recordId);
        final var recordDto = this.recordService.updateRecord(updateCommand);
        return this.recordMapper.toView(recordDto);
    }

    @DeleteMapping("/account-books/{accountBookId}/records/{recordId}")
    @Operation(summary = "Delete a record by ID")
    public void deleteRecord(@AuthenticationPrincipal JwtUserPayload jwtUserPayload,
                             @PathVariable UUID accountBookId,
                             @PathVariable UUID recordId) {
        this.memberValidator.checkUserCanModifyRecord(accountBookId, recordId,
                                                      jwtUserPayload.getId());
        this.recordValidator.checkRecordContainedInAccountBook(accountBookId, recordId);

        this.recordService.deleteRecord(recordId);
    }

}
