package me.redshore.web_gagebu.feature.accountbook.validation;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import me.redshore.web_gagebu.common.error.AppException;
import me.redshore.web_gagebu.common.error.ErrorCode;
import me.redshore.web_gagebu.feature.accountbook.domain.Record;
import me.redshore.web_gagebu.feature.accountbook.repository.RecordRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class RecordValidator {

    private final RecordRepository recordRepository;

    @Transactional(readOnly = true)
    public void checkRecordContainedInAccountBook(UUID accountBookId, UUID recordId) {
        Record record = this.recordRepository
            .findById(recordId)
            .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND,
                                                String.format("Record with ID '%s' not found",
                                                              recordId)));

        if (!record.getAccountBook().getId().equals(accountBookId)) {
            throw new AppException(ErrorCode.CONFLICT,
                                   String.format(
                                       "Record with ID '%s' does not belong to account book with ID '%s'",
                                       recordId, accountBookId));
        }
    }

}
