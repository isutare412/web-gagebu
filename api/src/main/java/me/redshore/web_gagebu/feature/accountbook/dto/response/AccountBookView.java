package me.redshore.web_gagebu.feature.accountbook.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.lang.Nullable;

public record AccountBookView(
    UUID id,

    @Schema(example = "My Account Book")
    String name,

    List<MemberView> members,
    List<CategoryView> categories,

    @Nullable UUID createdBy,
    ZonedDateTime createdAt,
    ZonedDateTime updatedAt
) {

}
