package me.redshore.web_gagebu.feature.user.dto.response;

import org.springframework.lang.Nullable;

public record GetUserInfoResponse(@Nullable UserView user) {

}
