package me.redshore.web_gagebu.feature.accountbook.service;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import me.redshore.web_gagebu.common.error.AppException;
import me.redshore.web_gagebu.common.error.ErrorCode;
import me.redshore.web_gagebu.feature.accountbook.dto.RecordCreateCommand;
import me.redshore.web_gagebu.feature.accountbook.dto.RecordDto;
import me.redshore.web_gagebu.feature.accountbook.dto.RecordListQuery;
import me.redshore.web_gagebu.feature.accountbook.dto.RecordListResult;
import me.redshore.web_gagebu.feature.accountbook.dto.RecordUpdateCommand;
import me.redshore.web_gagebu.feature.accountbook.mapping.RecordMapper;
import me.redshore.web_gagebu.feature.accountbook.repository.AccountBookRepository;
import me.redshore.web_gagebu.feature.accountbook.repository.CategoryRepository;
import me.redshore.web_gagebu.feature.accountbook.repository.RecordRepository;
import me.redshore.web_gagebu.feature.accountbook.validation.RecordValidator;
import me.redshore.web_gagebu.feature.user.repository.UserRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RecordService {

    private final UserRepository userRepository;
    private final RecordRepository recordRepository;
    private final CategoryRepository categoryRepository;
    private final AccountBookRepository accountBookRepository;
    private final RecordMapper recordMapper;
    private final RecordValidator recordValidator;

    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ADMIN') or @accountBookAuthorizer.canAccess(#accountBookId)")
    public RecordDto getRecord(UUID accountBookId, UUID recordId) {
        this.recordValidator.checkRecordContainedInAccountBook(accountBookId, recordId);

        return this.recordRepository
            .findById(recordId)
            .map(this.recordMapper::toDto)
            .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND,
                                                String.format("Record with id %s not found",
                                                              recordId)));
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ADMIN') or @accountBookAuthorizer.canAccess(#listQuery.accountBookId)")
    public RecordListResult listRecords(RecordListQuery listQuery) {
        final var categoryIds = this.categoryRepository.findIdsByAccountBookIdAndNameIn(
            listQuery.accountBookId(), listQuery.categories());

        final var recordPage = this.recordRepository.findAllByQuery(listQuery, categoryIds);
        final var recordDtos = recordPage.stream()
                                         .map(this.recordMapper::toSummaryDto)
                                         .toList();

        return RecordListResult.builder()
                               .records(recordDtos)
                               .page(recordPage.getPageable().getPageNumber())
                               .pageSize(recordPage.getPageable().getPageSize())
                               .totalPages(recordPage.getTotalPages())
                               .totalElements(recordPage.getTotalElements())
                               .build();
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN') or @accountBookAuthorizer.canAccess(#command.accountBookId)")
    public RecordDto createRecord(RecordCreateCommand command) {
        final var user = this.userRepository
            .findById(command.userId())
            .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND,
                                                String.format("User with id %s not found",
                                                              command.userId())));
        final var accountBook = this.accountBookRepository
            .findById(command.accountBookId())
            .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND,
                                                String.format("AccountBook with id %s not found",
                                                              command.accountBookId())));
        final var category = this.categoryRepository
            .findById(command.categoryId())
            .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND,
                                                String.format("Category with id %s not found",
                                                              command.categoryId())));

        var record = this.recordMapper.toEntity(command);
        record.setUser(user);
        record.setAccountBook(accountBook);
        record.setCategory(category);
        record = this.recordRepository.save(record);

        return this.recordMapper.toDto(record);
    }

    @Transactional
    @PreAuthorize(
        "hasRole('ADMIN') or @recordAuthorizer.canModify(#command.accountBookId, #command.recordId)")
    public RecordDto updateRecord(RecordUpdateCommand command) {
        this.recordValidator.checkRecordContainedInAccountBook(command.accountBookId(),
                                                               command.recordId());

        final var record = this.recordRepository
            .findById(command.recordId())
            .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND,
                                                String.format("Record with id %s not found",
                                                              command.recordId())));

        final var category = this.categoryRepository
            .findById(command.categoryId())
            .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND,
                                                String.format("Category with id %s not found",
                                                              command.categoryId())));

        record.setCategory(category);
        record.setRecordType(command.recordType());
        record.setAmount(command.amount());
        record.setSummary(command.summary());
        record.setDescription(command.description());
        record.setDate(command.date());

        return this.recordMapper.toDto(record);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN') or @recordAuthorizer.canModify(#accountBookId, #recordId)")
    public void deleteRecord(UUID accountBookId, UUID recordId) {
        this.recordValidator.checkRecordContainedInAccountBook(accountBookId, recordId);
        this.recordRepository.deleteById(recordId);
    }

}
