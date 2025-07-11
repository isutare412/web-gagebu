package me.redshore.web_gagebu.common.config.properties;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Configuration
@ConfigurationProperties("app.encryption")
@Validated
@Setter
@Getter
public class EncryptionProperties {

    @NotBlank
    private String key;

}
