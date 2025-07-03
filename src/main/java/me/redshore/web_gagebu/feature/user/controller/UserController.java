package me.redshore.web_gagebu.feature.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import me.redshore.web_gagebu.common.config.OpenApiConfig;
import me.redshore.web_gagebu.feature.auth.jwt.JwtUserPayload;
import me.redshore.web_gagebu.feature.user.dto.request.UserUpdateRequest;
import me.redshore.web_gagebu.feature.user.dto.response.GetCurrentUserResponse;
import me.redshore.web_gagebu.feature.user.dto.response.UserView;
import me.redshore.web_gagebu.feature.user.mapping.UserMapper;
import me.redshore.web_gagebu.feature.user.service.UserService;
import me.redshore.web_gagebu.feature.user.validation.UserValidator;
import org.springframework.lang.Nullable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "User")
@SecurityRequirements({
    @SecurityRequirement(name = OpenApiConfig.BEARER_TOKEN_AUTH),
    @SecurityRequirement(name = OpenApiConfig.COOKIE_TOKEN_AUTH)})
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;
    private final UserValidator userValidator;

    @GetMapping("/users/me")
    @Operation(
        summary = "Get current logged-in user information",
        description = "The user field is null if the user is not authenticated.")
    public GetCurrentUserResponse getCurrentUser(
        @Nullable @AuthenticationPrincipal JwtUserPayload jwtUserPayload) {

        return Optional.ofNullable(jwtUserPayload)
                       .map(payload -> this.userService.getUser(payload.getId()))
                       .map(userDto -> new GetCurrentUserResponse(this.userMapper.toView(userDto)))
                       .orElseGet(() -> new GetCurrentUserResponse(null));
    }

    @GetMapping("/users/{userId}")
    @Operation(
        summary = "Get user information by ID",
        description = "Returns user information for the specified user ID.")
    public UserView getUser(@AuthenticationPrincipal JwtUserPayload jwtUserPayload,
                            @PathVariable UUID userId) {
        this.userValidator.checkRequesterCanAccessUser(jwtUserPayload.getId(), userId);

        final var userDto = this.userService.getUser(userId);
        return this.userMapper.toView(userDto);
    }

    @PutMapping("/users/{userId}")
    @Operation(summary = "Update user data")
    public UserView updateUser(@AuthenticationPrincipal JwtUserPayload jwtUserPayload,
                               @PathVariable UUID userId,
                               @Valid @RequestBody UserUpdateRequest body) {
        this.userValidator.checkRequesterCanAccessUser(jwtUserPayload.getId(), userId);

        final var updateCommand = this.userMapper.toUpdateCommand(body, userId);
        final var userDto = this.userService.updateUser(updateCommand);
        return this.userMapper.toView(userDto);
    }

}
