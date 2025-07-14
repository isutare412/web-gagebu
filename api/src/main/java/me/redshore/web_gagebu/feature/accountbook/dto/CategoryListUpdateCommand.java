package me.redshore.web_gagebu.feature.accountbook.dto;

import java.util.List;
import java.util.UUID;
import lombok.Builder;

@Builder
public record CategoryListUpdateCommand(
    UUID accountBookId,
    List<CategoryUpdateCommand> categories
) {

}
