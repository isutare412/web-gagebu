package me.redshore.web_gagebu.accountbook.record;

import java.util.UUID;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.redshore.web_gagebu.accountbook.record.validator.RecordValidator;
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

    private final RecordValidator recordValidator;

    @GetMapping("/account-books/{accountBookId}/records/{recordId}")
    @Operation(summary = "Get record by ID")
    @PreAuthorize("@memberValidator.checkUserIsMemberOfAccountBook(#accountBookId)")
    public String getRecord(@PathVariable UUID accountBookId, @PathVariable UUID recordId) {
        this.recordValidator.checkRecordContainedInAccountBook(accountBookId, recordId);

        log.debug("Account Book ID: {}, Record ID: {}", accountBookId, recordId);
        return "Hello, world!";
    }

}
