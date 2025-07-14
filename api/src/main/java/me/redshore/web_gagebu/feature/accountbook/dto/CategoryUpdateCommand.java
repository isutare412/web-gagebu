package me.redshore.web_gagebu.feature.accountbook.dto;

import java.util.UUID;
import lombok.Builder;
import org.springframework.lang.Nullable;

@Builder
public record CategoryUpdateCommand(
    @Nullable
    UUID id,
    String name
) {

}
