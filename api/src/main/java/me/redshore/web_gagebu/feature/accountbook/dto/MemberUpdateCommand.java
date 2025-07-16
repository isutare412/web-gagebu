package me.redshore.web_gagebu.feature.accountbook.dto;

import java.util.UUID;
import lombok.Builder;
import me.redshore.web_gagebu.feature.accountbook.domain.MemberRole;

@Builder
public record MemberUpdateCommand(
    UUID accountBookId,
    UUID memberId,
    MemberRole role
) {

}
