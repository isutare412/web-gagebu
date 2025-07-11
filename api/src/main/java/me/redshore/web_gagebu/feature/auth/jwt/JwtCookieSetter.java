package me.redshore.web_gagebu.feature.auth.jwt;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtCookieSetter {

    public static final String TOKEN_COOKIE_NAME = "TOKEN";

    private final JwtProvider jwtProvider;

    public void setCookie(HttpServletResponse response, JwtUserPayload payload) {
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
