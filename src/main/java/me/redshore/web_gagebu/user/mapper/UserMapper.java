package me.redshore.web_gagebu.user.mapper;

import org.mapstruct.Mapper;
import me.redshore.web_gagebu.user.User;
import me.redshore.web_gagebu.user.dto.UserDto;
import me.redshore.web_gagebu.user.dto.UserJwtPayload;
import me.redshore.web_gagebu.user.dto.UserOidcUpsertCommand;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toEntity(UserOidcUpsertCommand command);

    UserDto toDto(User user);

    UserJwtPayload toJwtPayload(UserDto user);

}
