package me.redshore.web_gagebu.auth;

import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.redshore.web_gagebu.common.exception.auth.UnexpectedIdpException;
import me.redshore.web_gagebu.user.IdpType;
import me.redshore.web_gagebu.user.User;
import me.redshore.web_gagebu.user.UserService;
import me.redshore.web_gagebu.user.dto.UserOidcUpsertCommand;
import me.redshore.web_gagebu.user.mapper.UserMapper;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOidcUserService extends OidcUserService {

    private final UserService userService;

    private final UserMapper userMapper;

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) {
        OidcUser oidcUser = super.loadUser(userRequest);

        User user;
        switch (userRequest.getClientRegistration().getRegistrationId()) {
            case "google":
                var upsertCommand = createGoogleUser(oidcUser);
                user = this.userService.upsertUser(upsertCommand);
                break;

            default:
                throw new UnexpectedIdpException(
                    String.format("Unsupported OIDC provider: %s",
                                  userRequest.getClientRegistration().getRegistrationId()));
        }

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
