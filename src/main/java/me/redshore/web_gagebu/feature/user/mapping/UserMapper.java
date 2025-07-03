package me.redshore.web_gagebu.feature.user.mapping;

import java.util.UUID;
import me.redshore.web_gagebu.feature.auth.jwt.JwtUserPayload;
import me.redshore.web_gagebu.feature.user.domain.User;
import me.redshore.web_gagebu.feature.user.dto.UserDto;
import me.redshore.web_gagebu.feature.user.dto.UserOidcUpsertCommand;
import me.redshore.web_gagebu.feature.user.dto.UserUpdateCommand;
import me.redshore.web_gagebu.feature.user.dto.request.UserUpdateRequest;
import me.redshore.web_gagebu.feature.user.dto.response.UserView;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toEntity(UserOidcUpsertCommand command);

    UserDto toDto(User user);

    JwtUserPayload toJwtPayload(UserDto user);

    UserView toView(UserDto userDto);

    @Mapping(target = ".", source = "request")
    @Mapping(target = "userId", source = "userId")
    UserUpdateCommand toUpdateCommand(UserUpdateRequest request, UUID userId);

}
