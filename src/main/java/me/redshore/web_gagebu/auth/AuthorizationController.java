package me.redshore.web_gagebu.auth;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;


@RestController
@RequestMapping("/api/v1/oauth2")
@Tag(name = "Authentication")
public class AuthorizationController {

    @GetMapping("/authorization/google")
    @Operation(summary = "Start Google OAuth2 authorization flow")
    public void redirectToIdp() {
        throw new UnsupportedOperationException("This endpoint should not be called directly");
    }

}
