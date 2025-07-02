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
import me.redshore.web_gagebu.feature.user.repository.UserRepository;
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

    @Transactional(readOnly = true)
    public RecordDto getRecordById(UUID recordId) {
        return this.recordRepository
            .findById(recordId)
            .map(this.recordMapper::toDto)
            .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND,
                                                String.format("Record with id %s not found",
                                                              recordId)));
    }

    @Transactional(readOnly = true)
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
    public RecordDto updateRecord(RecordUpdateCommand command) {
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
    public void deleteRecord(UUID recordId) {
        this.recordRepository.deleteById(recordId);
    }

}
