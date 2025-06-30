package me.redshore.web_gagebu.common.config.properties;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Configuration
@ConfigurationProperties("app.category")
@Validated
@RequiredArgsConstructor
@Setter
@Getter
public class CategoryProperties {

    private List<String> defaults = new ArrayList<>();

}
