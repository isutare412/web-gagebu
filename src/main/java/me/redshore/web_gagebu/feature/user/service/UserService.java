package me.redshore.web_gagebu.feature.user.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import me.redshore.web_gagebu.feature.user.domain.UserRole;
import me.redshore.web_gagebu.feature.user.domain.User;
import me.redshore.web_gagebu.feature.user.dto.UserDto;
import me.redshore.web_gagebu.feature.user.dto.UserOidcUpsertCommand;
import me.redshore.web_gagebu.feature.user.mapping.UserMapper;
import me.redshore.web_gagebu.feature.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;

    private final UserRepository userRepository;

    @Transactional
    public UserDto upsertUser(UserOidcUpsertCommand command) {
        var optionalUser =
            this.userRepository.findByIdpTypeAndIdpIdentifier(command.idpType(),
                                                              command.idpIdentifier());

        User user;
        if (optionalUser.isPresent()) {
            user = optionalUser.get();
            mergeUser(user, command);
        } else {
            user = createUser(command);
        }

        user = this.userRepository.save(user);
        return this.userMapper.toDto(user);
    }

    private void mergeUser(User user, UserOidcUpsertCommand command) {
        user.setPictureUrl(command.pictureUrl());
        user.setEmail(command.email());
    }

    private User createUser(UserOidcUpsertCommand command) {
        var user = this.userMapper.toEntity(command);
        user.addRole(UserRole.USER);
        return user;
    }

}
