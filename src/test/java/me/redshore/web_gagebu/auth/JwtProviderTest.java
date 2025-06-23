package me.redshore.web_gagebu.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import me.redshore.web_gagebu.user.IdpType;
import me.redshore.web_gagebu.user.dto.UserJwtPayload;

@SpringBootTest
public class JwtProviderTest {

    @Autowired
    private JwtProvider jwtProvider;

    @Test
    void contextLoads() {
        assertNotNull(jwtProvider);
    }

    @Nested
    class Describe_parseToken {

        @Nested
        class Context_with_valid_payload {

            private UserJwtPayload userJwtPayload;

            @BeforeEach
            void setUp() {
                userJwtPayload = UserJwtPayload.builder()
                    .id(UUID.randomUUID())
                    .nickname("testuser")
                    .idpType(IdpType.GOOGLE)
                    .idpIdentifier("google-id-12345")
                    .pictureUrl("http://example.com/picture.jpg")
                    .email("tester@gmail.com")
                    .build();
            }

            @Test
            void it_should_parse_token() {
                String token = jwtProvider.createToken(this.userJwtPayload);
                UserJwtPayload payload = jwtProvider.parseToken(token);
                assertEquals(userJwtPayload, payload);
            }

        }

        @Nested
        class Context_with_null_fields {

            private UserJwtPayload samplePayload;

            @BeforeEach
            void setUp() {
                samplePayload = UserJwtPayload.builder()
                    .id(UUID.randomUUID())
                    .nickname("testuser")
                    .idpType(IdpType.GOOGLE)
                    .idpIdentifier("google-id-12345")
                    .pictureUrl(null)
                    .email(null)
                    .build();
            }

            @Test
            void it_should_parse_token() {
                String token = jwtProvider.createToken(this.samplePayload);
                UserJwtPayload payload = jwtProvider.parseToken(token);
                assertEquals(this.samplePayload, payload);
            }

        }

    }

    @Nested
    class Describe_validateToken {

        @Nested
        class Context_with_valid_token {

            private UserJwtPayload samplePayload;

            @BeforeEach
            void setUp() {
                samplePayload = UserJwtPayload.builder()
                    .id(UUID.randomUUID())
                    .nickname("testuser")
                    .idpType(IdpType.GOOGLE)
                    .idpIdentifier("google-id-12345")
                    .pictureUrl("http://example.com/picture.jpg")
                    .email("tester@gmail.com")
                    .build();
            }

            @Test
            void it_should_validate_token() {
                String token = jwtProvider.createToken(this.samplePayload);
                boolean isValid = jwtProvider.validateToken(token);
                assertEquals(true, isValid);
            }

        }

    }

}
