package me.redshore.web_gagebu.accountbook.record;

import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import me.redshore.web_gagebu.common.exception.AppException;
import me.redshore.web_gagebu.common.exception.ErrorCode;

@Service
@RequiredArgsConstructor
public class RecordService {

    private final RecordRepository recordRepository;

    @Transactional(readOnly = true)
    public void validateRecordId(UUID accountBookId, UUID recordId) {
        var record = this.recordRepository.findById(recordId)
            .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND,
                String.format("Record with ID '%s' not found", recordId)));

        if (!record.getAccountBook().getId().equals(accountBookId)) {
            throw new AppException(ErrorCode.CONFLICT,
                String.format("Record with ID '%s' does not belong to account book with ID '%s'",
                              recordId, accountBookId));
        }
    }

}
