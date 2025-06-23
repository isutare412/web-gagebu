package me.redshore.web_gagebu.common.config.properties;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Configuration
@ConfigurationProperties("app.invitation")
@Validated
@RequiredArgsConstructor
@Setter
@Getter
public class InvitationProperties {

    private Cleanup cleanup = new Cleanup();

    @Setter
    @Getter
    public static class Cleanup {

        private Duration rate = Duration.ofMinutes(5);

    }

}
