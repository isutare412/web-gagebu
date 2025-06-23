package me.redshore.web_gagebu.common.config.properties;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;
import lombok.Getter;
import lombok.Setter;

@Configuration
@ConfigurationProperties("app.auth")
@Validated
@Setter
@Getter
public class AuthProperties {

    private Jwt jwt = new Jwt();

    @Getter
    @Setter
    public static class Jwt {

        private String issuer = "web-gagebu";

        private Duration expiration = Duration.ofDays(7);

        private String privateKey = "";

        private String publicKey = "";

    }

}
