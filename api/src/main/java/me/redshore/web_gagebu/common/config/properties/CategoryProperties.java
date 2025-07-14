package me.redshore.web_gagebu.common.config.properties;

import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

    private @Valid List<DefaultCategory> defaults = new ArrayList<>();

    @Getter
    @Setter
    public static class DefaultCategory {

        @NotBlank(message = "Default category name must not be blank")
        private String name;

        @NotNull(message = "Default category fallback status must not be null")
        private Boolean isFallback = false;

    }

    @PostConstruct
    public void validateFallback() {
        long fallbackCount = defaults.stream().filter(DefaultCategory::getIsFallback).count();
        if (fallbackCount != 1) {
            throw new ValidationException("Only one fallback category is allowed in app.category.defaults");
        }
    }

}
