package me.redshore.web_gagebu.feature.accountbook.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import me.redshore.web_gagebu.common.config.OpenApiConfig;
import me.redshore.web_gagebu.feature.accountbook.dto.request.RecordCreateRequest;
import me.redshore.web_gagebu.feature.accountbook.dto.request.RecordListRequest;
import me.redshore.web_gagebu.feature.accountbook.dto.request.RecordUpdateRequest;
import me.redshore.web_gagebu.feature.accountbook.dto.response.RecordListResponse;
import me.redshore.web_gagebu.feature.accountbook.dto.response.RecordView;
import me.redshore.web_gagebu.feature.accountbook.mapping.RecordMapper;
import me.redshore.web_gagebu.feature.accountbook.service.RecordService;
import me.redshore.web_gagebu.feature.auth.jwt.JwtUserPayload;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@SecurityRequirements({
    @SecurityRequirement(name = OpenApiConfig.BEARER_TOKEN_AUTH),
    @SecurityRequirement(name = OpenApiConfig.COOKIE_TOKEN_AUTH)})
@Tag(name = "Record")
public class RecordController {

    private final RecordMapper recordMapper;
    private final RecordService recordService;

    @GetMapping("/account-books/{accountBookId}/records/{recordId}")
    @Operation(summary = "Get record by ID")
    public RecordView getRecord(@PathVariable UUID accountBookId, @PathVariable UUID recordId) {
        final var recordDto = this.recordService.getRecord(accountBookId, recordId);
        return this.recordMapper.toView(recordDto);
    }

    @GetMapping("/account-books/{accountBookId}/records")
    @Operation(summary = "List records of account book")
    public RecordListResponse listRecords(@PathVariable UUID accountBookId,
                                          @Valid @ParameterObject @ModelAttribute RecordListRequest request) {
        final var listQuery = this.recordMapper.toListQuery(request, accountBookId);
        final var listResult = this.recordService.listRecords(listQuery);
        return this.recordMapper.toListResponse(listResult);
    }

    @PostMapping("/account-books/{accountBookId}/records")
    @Operation(summary = "Create a new record")
    public RecordView createRecord(@AuthenticationPrincipal JwtUserPayload jwtUserPayload,
                                   @PathVariable UUID accountBookId,
                                   @Valid @RequestBody RecordCreateRequest body) {
        final var createCommand = this.recordMapper.toCreateCommand(body, jwtUserPayload.getId(),
                                                                    accountBookId);
        final var recordDto = this.recordService.createRecord(createCommand);
        return this.recordMapper.toView(recordDto);
    }

    @PutMapping("/account-books/{accountBookId}/records/{recordId}")
    @Operation(summary = "Update an existing record")
    public RecordView updateRecord(@PathVariable UUID accountBookId,
                                   @PathVariable UUID recordId,
                                   @Valid @RequestBody RecordUpdateRequest body) {
        final var updateCommand = this.recordMapper.toUpdateCommand(body, accountBookId, recordId);
        final var recordDto = this.recordService.updateRecord(updateCommand);
        return this.recordMapper.toView(recordDto);
    }

    @DeleteMapping("/account-books/{accountBookId}/records/{recordId}")
    @Operation(summary = "Delete a record by ID")
    public void deleteRecord(@PathVariable UUID accountBookId,
                             @PathVariable UUID recordId) {
        this.recordService.deleteRecord(accountBookId, recordId);
    }

}
