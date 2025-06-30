package me.redshore.web_gagebu.feature.accountbook.dto;

import java.util.UUID;
import lombok.Builder;

@Builder
public record AccountBookCreateCommand(
    UUID userId,
    String accountBookName
) {

}
