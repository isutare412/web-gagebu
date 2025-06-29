package me.redshore.web_gagebu.feature.auth.oidc;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class OidcCookieManager {

    private static final String REDIRECT_URI_COOKIE_NAME = "REDIRECT_URI";

    public void setRedirectUri(HttpServletResponse response, String redirectUri) {
        final var cookie = new Cookie(REDIRECT_URI_COOKIE_NAME, redirectUri);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60); // 1 hour
        cookie.setAttribute("SameSite", "Lax");
        response.addCookie(cookie);
    }

    public Optional<String> getRedirectUri(HttpServletRequest request) {
        return Optional.ofNullable(request.getCookies())
                       .stream()
                       .flatMap(Arrays::stream)
                       .filter(cookie -> cookie.getName().equals(REDIRECT_URI_COOKIE_NAME))
                       .findFirst()
                       .map(Cookie::getValue);
    }
}
