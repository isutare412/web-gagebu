package me.redshore.web_gagebu.feature.auth.oidc;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.redshore.web_gagebu.feature.auth.jwt.JwtProvider;

@Component
@RequiredArgsConstructor
@Slf4j
public class OidcSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    public static final String TOKEN_COOKIE_NAME = "TOKEN";

    private final JwtProvider jwtProvider;

    @Override
    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) {
        setTokenCookie(response, authentication);

        HttpSession session = request.getSession();
        var redirectUri = (String) session.getAttribute(OidcRequestResolver.REDIRECT_URI_KEY);

        if (!StringUtils.hasText(redirectUri)) {
            String defaultUri = super.determineTargetUrl(request, response);
            log.info("No referer found, redirecting to default URL: '{}'", defaultUri);
            return defaultUri;
        }
        session.removeAttribute(OidcRequestResolver.REDIRECT_URI_KEY);

        log.info("Referer found in session, redirecting to: '{}'", redirectUri);
        return redirectUri;
    }

    private void setTokenCookie(HttpServletResponse response, Authentication authentication) {
        if (!(authentication.getPrincipal() instanceof CustomOidcUser)) {
            return;
        }

        var oidcUser = (CustomOidcUser) authentication.getPrincipal();
        var payload = oidcUser.getUserJwtPayload();
        var token = jwtProvider.createToken(payload);

        var cookie = new Cookie(TOKEN_COOKIE_NAME, token);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge((int) (this.jwtProvider.getExpiration().toSeconds() - 60));
        cookie.setAttribute("SameSite", "Lax");
        response.addCookie(cookie);
    }

}
