package me.redshore.web_gagebu.feature.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import me.redshore.web_gagebu.feature.auth.jwt.JwtUserPayload;
import me.redshore.web_gagebu.feature.user.dto.response.GetUserInfoResponse;
import me.redshore.web_gagebu.feature.user.mapping.UserMapper;
import me.redshore.web_gagebu.feature.user.service.UserService;
import org.springframework.lang.Nullable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "User")
public class UserController {

    private final UserService userService;

    private final UserMapper userMapper;

    @GetMapping("/users/me")
    @Operation(
        summary = "Get current logged-in user information",
        description = "The user field is null if the user is not authenticated.")
    public GetUserInfoResponse getUserInfo(
        @Nullable @AuthenticationPrincipal JwtUserPayload jwtUserPayload) {

        return Optional.ofNullable(jwtUserPayload)
                       .map(payload -> this.userService.getUser(payload.getId()))
                       .map(userDto -> new GetUserInfoResponse(this.userMapper.toView(userDto)))
                       .orElseGet(() -> new GetUserInfoResponse(null));
    }

}
