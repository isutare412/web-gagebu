package me.redshore.web_gagebu.feature.auth.oidc;

import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.redshore.web_gagebu.common.error.AppException;
import me.redshore.web_gagebu.common.error.ErrorCode;
import me.redshore.web_gagebu.feature.user.domain.IdpType;
import me.redshore.web_gagebu.feature.user.dto.UserDto;
import me.redshore.web_gagebu.feature.user.dto.UserOidcUpsertCommand;
import me.redshore.web_gagebu.feature.user.mapping.UserMapper;
import me.redshore.web_gagebu.feature.user.service.UserService;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOidcUserService extends OidcUserService {

    private final UserService userService;

    private final UserMapper userMapper;

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) {
        OidcUser oidcUser = super.loadUser(userRequest);

        UserDto user = switch (userRequest.getClientRegistration().getRegistrationId()) {
            case "google" -> {
                var upsertCommand = createGoogleUser(oidcUser);
                yield this.userService.upsertUser(upsertCommand);
            }
            default -> {
                throw new AppException(ErrorCode.BAD_REQUEST,
                    String.format("Unsupported OIDC provider: '%s'",
                                  userRequest.getClientRegistration().getRegistrationId()));
            }
        };

        return new CustomOidcUser(oidcUser, userMapper.toJwtPayload(user));
    }

    private UserOidcUpsertCommand createGoogleUser(OidcUser user) {
        return UserOidcUpsertCommand.builder()
            .nickname(user.getFullName())
            .idpType(IdpType.GOOGLE)
            .idpIdentifier(user.getSubject())
            .pictureUrl(user.getPicture())
            .email(user.getEmail())
            .build();
    }

}
