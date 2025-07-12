package me.redshore.web_gagebu.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "app.cors")
public class CorsProperties {

    /**
     * List of allowed origins for CORS requests.
     * Can use patterns like "http://localhost:*"
     */
    private List<String> allowedOrigins = List.of("http://localhost:*", "https://localhost:*");

    /**
     * List of allowed HTTP methods for CORS requests.
     * Use "*" to allow all methods.
     */
    private List<String> allowedMethods = List.of("*");

    /**
     * List of allowed headers for CORS requests.
     * Use "*" to allow all headers.
     */
    private List<String> allowedHeaders = List.of("*");

    /**
     * Whether to allow credentials (cookies, authorization headers) in CORS requests.
     */
    private boolean allowCredentials = true;

    /**
     * Path pattern to apply CORS configuration to.
     * Default is "/**" (all paths).
     */
    private String pathPattern = "/**";
}
