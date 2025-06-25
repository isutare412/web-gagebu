package me.redshore.web_gagebu.user;

import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import me.redshore.web_gagebu.user.dto.UserDto;
import me.redshore.web_gagebu.user.dto.UserOidcUpsertCommand;
import me.redshore.web_gagebu.user.mapper.UserMapper;

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
        user.addRole(Role.USER);
        return user;
    }

}
