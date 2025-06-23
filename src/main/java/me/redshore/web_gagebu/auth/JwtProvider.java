package me.redshore.web_gagebu.auth;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import me.redshore.web_gagebu.config.properties.AuthProperties;
import me.redshore.web_gagebu.user.dto.UserJwtPayload;

@Component
@Slf4j
public class JwtProvider implements JwtDecoder {

    private final PrivateKey privateKey;

    private final PublicKey publicKey;

    private final AuthProperties authProperties;

    private final JwtParser jwtParser;

    public JwtProvider(AuthProperties authProperties)
        throws NoSuchAlgorithmException, InvalidKeySpecException {
        this.authProperties = authProperties;
        this.privateKey = loadPrivateKey(authProperties.getJwt().getPrivateKey());
        this.publicKey = loadPublicKey(authProperties.getJwt().getPublicKey());
        this.jwtParser = Jwts.parser().verifyWith(this.publicKey).build();
    }

    public String createToken(UserJwtPayload user) {
        var now = new Date();
        var expiration =
            new Date(now.getTime() + this.authProperties.getJwt().getExpiration().toMillis());

        Claims claims = Jwts.claims()
            .subject(user.id().toString())
            .issuer(this.authProperties.getJwt().getIssuer())
            .issuedAt(now)
            .notBefore(now)
            .expiration(expiration)
            .add(user.toMap())
            .build();

        return Jwts.builder()
            .claims(claims)
            .signWith(this.privateKey)
            .compact();
    }

    public UserJwtPayload parseToken(String token)
        throws UnsupportedJwtException, JwtException, IllegalArgumentException {
        Claims claims = parseClaims(token).getPayload();
        return new UserJwtPayload(claims);
    }

    public boolean validateToken(String token) {
        try {
            this.jwtParser.parseSignedClaims(token);
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        } catch (Exception e) {
            log.error("Unexpected error while validating JWT token: {}", e.getMessage());
            return false;
        }
        return true;
    }

    public Duration getExpiration() {
        return this.authProperties.getJwt().getExpiration();
    }

    @Override
    public Jwt decode(String token) throws org.springframework.security.oauth2.jwt.JwtException {
        try {
            Jws<Claims> jws = parseClaims(token);
            Claims claims = jws.getPayload();

            Instant issuedAt =
                claims.getIssuedAt() != null ? claims.getIssuedAt().toInstant() : null;

            Instant expiresAt =
                claims.getExpiration() != null ? claims.getExpiration().toInstant() : null;

            return new Jwt(token, issuedAt, expiresAt, jws.getHeader(), claims);

        } catch (SignatureException e) {
            throw new org.springframework.security.oauth2.jwt.BadJwtException(
                "JWT signature is invalid: " + e.getMessage(), e);
        } catch (ExpiredJwtException e) {
            throw new org.springframework.security.oauth2.jwt.JwtValidationException(
                "JWT is expired: " + e.getMessage(), Collections.emptyList());
        } catch (MalformedJwtException | UnsupportedJwtException e) {
            throw new org.springframework.security.oauth2.jwt.BadJwtException(
                "Invalid JWT format: " + e.getMessage(), e);
        } catch (JwtException e) {
            throw new org.springframework.security.oauth2.jwt.JwtException(
                "Failed to decode JWT: " + e.getMessage(), e);
        }
    }

    private PrivateKey loadPrivateKey(String key)
        throws NoSuchAlgorithmException, InvalidKeySpecException {
        String privateKeyPEM = key
            .replace("-----BEGIN PRIVATE KEY-----", "")
            .replace("-----END PRIVATE KEY-----", "")
            .replace("-----BEGIN RSA PRIVATE KEY-----", "")
            .replace("-----END RSA PRIVATE KEY-----", "")
            .replaceAll("\\s", "");

        byte[] decoded = Base64.getDecoder().decode(privateKeyPEM);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decoded);
        return keyFactory.generatePrivate(keySpec);
    }

    private PublicKey loadPublicKey(String key)
        throws NoSuchAlgorithmException, InvalidKeySpecException {
        String publicKeyPEM = key
            .replace("-----BEGIN PUBLIC KEY-----", "")
            .replace("-----END PUBLIC KEY-----", "")
            .replaceAll("\\s", "");

        byte[] decoded = Base64.getDecoder().decode(publicKeyPEM);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decoded);
        return keyFactory.generatePublic(keySpec);
    }

    private Jws<Claims> parseClaims(String token) throws JwtException, IllegalArgumentException {
        return this.jwtParser.parseSignedClaims(token);
    }

}

