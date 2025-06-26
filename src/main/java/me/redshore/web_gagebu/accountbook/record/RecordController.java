package me.redshore.web_gagebu.accountbook.record;

import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.redshore.web_gagebu.accountbook.record.aop.CheckRecordAssociation;
import me.redshore.web_gagebu.common.config.OpenApiConfig;


@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@SecurityRequirements({
    @SecurityRequirement(name = OpenApiConfig.BEARER_TOKEN_AUTH),
    @SecurityRequirement(name = OpenApiConfig.COOKIE_TOKEN_AUTH)
})
@Tag(name = "Record")
@Slf4j
public class RecordController {

    @GetMapping("/account-books/{accountBookId}/records/{recordId}")
    @CheckRecordAssociation(accountBookIdKey = "accountBookId", recordIdKey = "recordId")
    public String getMethodName(@PathVariable UUID accountBookId, @PathVariable UUID recordId) {

        log.debug("Account Book ID: {}, Record ID: {}", accountBookId, recordId);
        return "Hello, world!";
    }

}
