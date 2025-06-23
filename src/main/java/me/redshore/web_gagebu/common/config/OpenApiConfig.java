package me.redshore.web_gagebu.common.config;

import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import me.redshore.web_gagebu.common.exception.ErrorResponse;

@Configuration
public class OpenApiConfig {

    @Bean
    OpenApiCustomizer openApiCustomizer() {
        var errorApiResponse = new ApiResponse()
            .description("Error response")
            .content(new Content()
                .addMediaType(org.springframework.http.MediaType.APPLICATION_JSON_VALUE,
                              new MediaType().schema(new Schema<>()
                                  .$ref("#/components/schemas/ErrorResponse"))));

        return openApi -> {
            // Register ErrorResponse schema
            var errorResponseSchema = ModelConverters.getInstance()
                .resolveAsResolvedSchema(new AnnotatedType(ErrorResponse.class)).schema;
            openApi.getComponents().addSchemas("ErrorResponse", errorResponseSchema);

            // Add default error response to all operations
            openApi.getPaths().values().forEach(pathItem -> {
                pathItem.readOperations().forEach(operation -> {
                    operation.getResponses().addApiResponse("default", errorApiResponse);
                });
            });
        };
    }

}
