package me.redshore.web_gagebu.accountbook.record.aop;

import java.util.Map;
import java.util.UUID;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.HandlerMapping;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.redshore.web_gagebu.accountbook.record.RecordService;
import me.redshore.web_gagebu.common.exception.AppException;
import me.redshore.web_gagebu.common.exception.ErrorCode;

@Component
@Aspect
@RequiredArgsConstructor
@Slf4j
public class RecordAssociationAspect {

    private final RecordService recordService;

    @Before("@annotation(check)")
    public void checkAssociation(JoinPoint joinPoint, CheckRecordAssociation check) {
        var request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
            .getRequest();

        @SuppressWarnings("unchecked")
        var pathVariables = (Map<String, String>) request
            .getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);

        var accountBookIdStr = pathVariables.get(check.accountBookIdKey());
        var recordIdStr = pathVariables.get(check.recordIdKey());
        log.debug("Checking association for account book ID: {}, record ID: {}",
                  accountBookIdStr, recordIdStr);

        if (accountBookIdStr == null || recordIdStr == null) {
            throw new AppException(ErrorCode.BAD_REQUEST,
                "Account book ID or record ID is missing in the request.");
        }

        UUID accountBookId;
        UUID recordId;
        try {
            accountBookId = UUID.fromString(accountBookIdStr);
            recordId = UUID.fromString(recordIdStr);
        } catch (IllegalArgumentException ex) {
            throw new AppException(ErrorCode.BAD_REQUEST,
                "Invalid UUID format for account book ID or record ID.");
        }

        recordService.validateRecordId(accountBookId, recordId);
    }

}
