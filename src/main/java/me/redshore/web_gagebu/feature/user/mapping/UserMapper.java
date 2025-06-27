package me.redshore.web_gagebu.feature.user.mapping;

import me.redshore.web_gagebu.feature.auth.jwt.JwtUserPayload;
import me.redshore.web_gagebu.feature.user.domain.User;
import me.redshore.web_gagebu.feature.user.dto.UserDto;
import me.redshore.web_gagebu.feature.user.dto.UserOidcUpsertCommand;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toEntity(UserOidcUpsertCommand command);

    UserDto toDto(User user);

    JwtUserPayload toJwtPayload(UserDto user);

}
