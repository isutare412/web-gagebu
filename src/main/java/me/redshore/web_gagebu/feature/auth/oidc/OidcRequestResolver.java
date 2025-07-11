package me.redshore.web_gagebu.feature.auth.oidc;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import me.redshore.web_gagebu.common.config.SecurityConfig;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class OidcRequestResolver implements OAuth2AuthorizationRequestResolver {

    private final DefaultOAuth2AuthorizationRequestResolver defaultResolver;

    public OidcRequestResolver(ClientRegistrationRepository registrationRepository) {
        this.defaultResolver =
            new DefaultOAuth2AuthorizationRequestResolver(registrationRepository,
                                                          SecurityConfig.AUTHZ_BASE_URI);
        this.defaultResolver.setAuthorizationRequestCustomizer(builder -> builder
            .additionalParameters(Map.of("prompt", "consent")));
    }

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest httpRequest) {
        OAuth2AuthorizationRequest oauthRequest = this.defaultResolver.resolve(httpRequest);
        if (oauthRequest == null) {
            return null;
        }

        onOAuth2Request(oauthRequest);

        return oauthRequest;
    }

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest httpRequest,
                                              String clientRegistrationId) {
        OAuth2AuthorizationRequest oauthRequest =
            this.defaultResolver.resolve(httpRequest, clientRegistrationId);
        if (oauthRequest == null) {
            return null;
        }

        onOAuth2Request(oauthRequest);

        return oauthRequest;
    }

    private void onOAuth2Request(OAuth2AuthorizationRequest request) {
        log.info("Start OIDC flow at {}", request.getAuthorizationUri());
    }

}
