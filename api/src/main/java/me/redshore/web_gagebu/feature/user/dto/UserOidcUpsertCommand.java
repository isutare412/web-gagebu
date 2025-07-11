package me.redshore.web_gagebu.feature.user.dto;

import lombok.Builder;
import me.redshore.web_gagebu.feature.user.domain.IdpType;

@Builder
public record UserOidcUpsertCommand(String nickname, IdpType idpType, String idpIdentifier,
                                    String pictureUrl, String email) {

}
