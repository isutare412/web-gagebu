package me.redshore.web_gagebu.feature.auth.oidc;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.security.GeneralSecurityException;
import java.time.Duration;
import java.util.Arrays;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import me.redshore.web_gagebu.common.error.AppException;
import me.redshore.web_gagebu.common.error.ErrorCode;
import me.redshore.web_gagebu.common.utils.AesEncryptor;
import org.apache.commons.lang3.SerializationUtils;
import org.springframework.lang.Nullable;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OidcRequestRepository implements
                                   AuthorizationRequestRepository<OAuth2AuthorizationRequest> {

    private static final String OIDC_REQUEST_COOKIE_NAME = "OIDC_REQUEST";
    private static final Duration OIDC_REQUEST_COOKIE_MAX_AGE_SECONDS = Duration.ofMinutes(60);

    private final AesEncryptor aesEncryptor;

    private final OidcCookieManager cookieManager;

    @Override
    public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest request) {
        final var state = getStateParameter(request);
        if (state == null) {
            return null;
        }

        return getAuthorizationRequestFromCookie(request)
            .filter(authorizationRequest -> authorizationRequest.getState().equals(state))
            .orElse(null);
    }

    @Override
    public void saveAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest,
                                         HttpServletRequest request, HttpServletResponse response) {
        if (authorizationRequest == null) {
            removeAuthorizationRequest(request, response);
            return;
        }

        setAuthorizationRequestToCookie(response, authorizationRequest);
        setRefererToCookie(request, response);
    }

    @Override
    public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request,
                                                                 HttpServletResponse response) {
        return getAuthorizationRequestFromCookie(request)
            .map(authorizationRequest -> {
                deleteAuthorizationRequestFromCookie(response);
                return authorizationRequest;
            }).orElse(null);
    }

    private @Nullable String getStateParameter(HttpServletRequest request) {
        return request.getParameter(OAuth2ParameterNames.STATE);
    }

    private Optional<OAuth2AuthorizationRequest> getAuthorizationRequestFromCookie(
        HttpServletRequest request) {
        return Arrays.stream(request.getCookies())
                     .filter(cookie -> cookie.getName().equals(OIDC_REQUEST_COOKIE_NAME))
                     .findFirst()
                     .map(cookie -> {
                         try {
                             return this.aesEncryptor.decrypt(cookie.getValue());
                         } catch (GeneralSecurityException ex) {
                             throw new AppException(ErrorCode.BAD_REQUEST,
                                                    "Failed to decrypt OIDC request", ex);
                         }
                     })
                     .map(SerializationUtils::deserialize);
    }

    private void setAuthorizationRequestToCookie(HttpServletResponse response,
                                                 OAuth2AuthorizationRequest authorizationRequest) {
        Optional.of(authorizationRequest)
                .map(SerializationUtils::serialize)
                .map(requestBytes -> {
                    try {
                        return this.aesEncryptor.encrypt(requestBytes);
                    } catch (GeneralSecurityException e) {
                        throw new AppException(ErrorCode.INTERNAL_SERVER_ERROR,
                                               "Failed to encrypt OIDC request",
                                               e);
                    }
                })
                .map(this::buildCookieBase)
                .ifPresent(cookie -> {
                    cookie.setMaxAge((int) OIDC_REQUEST_COOKIE_MAX_AGE_SECONDS.toSeconds());
                    response.addCookie(cookie);
                });
    }

    private void deleteAuthorizationRequestFromCookie(HttpServletResponse response) {
        var cookie = buildCookieBase("");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

    private void setRefererToCookie(HttpServletRequest request, HttpServletResponse response) {
        Optional.ofNullable(request.getHeader("Referer"))
                .ifPresent(referer -> this.cookieManager.setRedirectUri(response, referer));
    }

    private Cookie buildCookieBase(String value) {
        var cookie = new Cookie(OIDC_REQUEST_COOKIE_NAME, value);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setAttribute("SameSite", "Lax");
        return cookie;
    }
}
