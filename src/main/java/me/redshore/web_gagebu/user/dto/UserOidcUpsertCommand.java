package me.redshore.web_gagebu.user.dto;

import lombok.Builder;
import me.redshore.web_gagebu.user.IdpType;

@Builder
public record UserOidcUpsertCommand(String nickname, IdpType idpType, String idpIdentifier,
                                    String pictureUrl, String email) {
}
