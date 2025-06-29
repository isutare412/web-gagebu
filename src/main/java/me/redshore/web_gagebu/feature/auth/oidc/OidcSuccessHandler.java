package me.redshore.web_gagebu.feature.auth.oidc;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.redshore.web_gagebu.feature.auth.jwt.JwtCookieSetter;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
@Slf4j
public class OidcSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtCookieSetter jwtCookieSetter;

    private final OidcCookieManager cookieManager;

    @Override
    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) {
        setTokenCookie(response, authentication);

        final var redirectUri =
            this.cookieManager.getRedirectUri(request)
                              .filter(StringUtils::hasText)
                              .orElseGet(() -> super.determineTargetUrl(request, response));

        log.info("Finish OIDC flow. Redirect to '{}'", redirectUri);
        return redirectUri;
    }

    private void setTokenCookie(HttpServletResponse response, Authentication authentication) {
        Optional.of(authentication.getPrincipal())
                .filter(CustomOidcUser.class::isInstance)
                .map(CustomOidcUser.class::cast)
                .map(CustomOidcUser::getUserJwtPayload)
                .ifPresent(payload -> this.jwtCookieSetter.setCookie(response, payload));
    }

}
