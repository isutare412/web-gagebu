package me.redshore.web_gagebu.feature.auth.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;



@RestController
@RequestMapping("/api/v1")
@Tag(name = "Authentication")
public class AuthorizationController {

    /**
     * This endpoint is just for OpenAPI documentation purposes.
     */
    @GetMapping("/oauth2/authorization/google")
    @Operation(summary = "Start Google OAuth2 authorization flow")
    public void redirectToIdp() {
        throw new UnsupportedOperationException("This endpoint should not be called directly");
    }

    /**
     * This endpoint is just for OpenAPI documentation purposes.
     */
    @GetMapping("/logout")
    @Operation(summary = "Logout from the application")
    public void logout() {
        throw new UnsupportedOperationException("This endpoint should not be called directly");
    }


}
