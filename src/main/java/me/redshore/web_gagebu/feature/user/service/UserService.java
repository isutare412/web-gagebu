package me.redshore.web_gagebu.feature.user.service;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import me.redshore.web_gagebu.common.error.AppException;
import me.redshore.web_gagebu.common.error.ErrorCode;
import me.redshore.web_gagebu.feature.user.domain.User;
import me.redshore.web_gagebu.feature.user.domain.UserRole;
import me.redshore.web_gagebu.feature.user.dto.UserDto;
import me.redshore.web_gagebu.feature.user.dto.UserOidcUpsertCommand;
import me.redshore.web_gagebu.feature.user.mapping.UserMapper;
import me.redshore.web_gagebu.feature.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;

    private final UserRepository userRepository;

    public UserDto getUser(UUID id) {
        return this.userRepository.findById(id)
                                  .map(this.userMapper::toDto)
                                  .orElseThrow(
                                      () -> new AppException(ErrorCode.NOT_FOUND, String.format(
                                          "User with ID '%s' not found", id)));
    }

    @Transactional
    public UserDto upsertUser(UserOidcUpsertCommand command) {
        User user = this.userRepository.findByIdpTypeAndIdpIdentifier(command.idpType(),
                                                                      command.idpIdentifier())
                                       .map(found -> mergeUser(found, command))
                                       .orElseGet(() -> createUser(command));

        user = this.userRepository.save(user);
        return this.userMapper.toDto(user);
    }

    private User mergeUser(User user, UserOidcUpsertCommand command) {
        user.setPictureUrl(command.pictureUrl());
        user.setEmail(command.email());
        return user;
    }

    private User createUser(UserOidcUpsertCommand command) {
        var user = this.userMapper.toEntity(command);
        user.addRole(UserRole.USER);
        return user;
    }
}
