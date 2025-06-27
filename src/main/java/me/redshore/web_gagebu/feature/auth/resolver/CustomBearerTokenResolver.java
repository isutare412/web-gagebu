package me.redshore.web_gagebu.feature.auth.resolver;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import me.redshore.web_gagebu.feature.auth.oidc.OidcSuccessHandler;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.server.resource.BearerTokenErrors;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CustomBearerTokenResolver implements BearerTokenResolver {

    @Override
    public String resolve(HttpServletRequest request) {
        var token = resolveTokens(resolveFromHeader(request),
                                  resolveFromCookie(request));
        return token;
    }

    private static String resolveTokens(String... tokens) {
        if (tokens == null || tokens.length == 0) {
            return null;
        }

        String selectedToken = null;
        for (String token : tokens) {
            if (token != null) {
                selectedToken = token;
                break;
            }
        }

        if (selectedToken != null && selectedToken.isBlank()) {
            throw new OAuth2AuthenticationException(
                BearerTokenErrors.invalidRequest("The requested token is empty"));
        }

        return selectedToken;
    }

    private static String resolveFromHeader(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        if (authorization != null && authorization.startsWith("Bearer ")) {
            return authorization.substring(7);
        }
        return null;
    }

    private static String resolveFromCookie(HttpServletRequest request) {
        var cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }

        for (var cookie : cookies) {
            if (OidcSuccessHandler.TOKEN_COOKIE_NAME.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }

}
