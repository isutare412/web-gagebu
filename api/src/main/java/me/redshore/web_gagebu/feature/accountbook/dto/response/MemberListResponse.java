package me.redshore.web_gagebu.feature.accountbook.dto.response;

import java.util.List;
import lombok.Builder;

@Builder
public record MemberListResponse(
    List<MemberView> members
) {

}
