package me.redshore.web_gagebu.feature.accountbook.dto.request;

import me.redshore.web_gagebu.feature.accountbook.domain.MemberRole;

public record MemberUpdateRequest(
    MemberRole role
) {

}
