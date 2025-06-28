package me.redshore.web_gagebu.common.config;

import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.security.SecurityScheme;
import me.redshore.web_gagebu.common.error.ErrorResponse;
import me.redshore.web_gagebu.feature.auth.jwt.JwtCookieSetter;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    public static final String BEARER_TOKEN_AUTH = "bearerTokenAuth";

    public static final String COOKIE_TOKEN_AUTH = "cookieTokenAuth";

    @Bean
    OpenAPI customOpenApi() {
        return new OpenAPI()
            .components(new Components()
                            .addSecuritySchemes(BEARER_TOKEN_AUTH, new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT"))
                            .addSecuritySchemes(COOKIE_TOKEN_AUTH, new SecurityScheme()
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.COOKIE)
                                .name(JwtCookieSetter.TOKEN_COOKIE_NAME)))
            .info(new Info()
                      .title("Web Gagebu API")
                      .version("0.1.0"));
    }

    @Bean
    OpenApiCustomizer openApiCustomizer() {
        return this::addDefaultErrorResponse;
    }

    private void addDefaultErrorResponse(OpenAPI openApi) {
        // Register ErrorResponse schema
        var errorResponseSchema = ModelConverters.getInstance()
                                                 .resolveAsResolvedSchema(
                                                     new AnnotatedType(ErrorResponse.class)).schema;
        openApi.getComponents().addSchemas("ErrorResponse", errorResponseSchema);

        // Create default error response
        var errorApiResponse = new ApiResponse()
            .description("Error response")
            .content(new Content()
                         .addMediaType(org.springframework.http.MediaType.APPLICATION_JSON_VALUE,
                                       new MediaType().schema(new Schema<>()
                                                                  .$ref(
                                                                      "#/components/schemas/ErrorResponse"))));

        // Add default error response to all operations
        openApi.getPaths().values()
               .forEach(pathItem -> pathItem.readOperations()
                                            .forEach(operation -> operation
                                                .getResponses()
                                                .addApiResponse("default", errorApiResponse)));
    }

}
