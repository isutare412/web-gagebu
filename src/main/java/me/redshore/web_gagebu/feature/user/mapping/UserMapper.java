package me.redshore.web_gagebu.feature.user.mapping;

import org.mapstruct.Mapper;
import me.redshore.web_gagebu.feature.auth.jwt.JwtUserPayload;
import me.redshore.web_gagebu.feature.user.domain.User;
import me.redshore.web_gagebu.feature.user.dto.UserDto;
import me.redshore.web_gagebu.feature.user.dto.UserOidcUpsertCommand;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toEntity(UserOidcUpsertCommand command);

    UserDto toDto(User user);

    JwtUserPayload toJwtPayload(UserDto user);

}
